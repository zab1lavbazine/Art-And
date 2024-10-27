package art.example.api.repository.impl

import android.content.Context
import android.util.Log
import androidx.room.Transaction
import art.example.api.data.DTO.FolderDTO
import art.example.api.data.Folder
import art.example.api.data.Post
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsClient
import art.example.api.data.User
import art.example.api.data.toFolderEntity
import art.example.api.data.toPostEntity
import art.example.api.data.toTagEntity
import art.example.api.reponses.UserCredentials
import art.example.api.repository.IUserRepository
import art.example.api.service.FolderApiService
import art.example.api.service.UserApiService
import art.example.database.PostDao.PostDao
import art.example.database.TagDao.TagDao
import art.example.database.UserDao.UserDao
import art.example.database.entities.FolderWithPostsCrossRef
import art.example.database.entities.UserEntity
import art.example.database.entities.UserWithTagsCrossRef
import art.example.database.entities.toFolder
import art.example.database.entities.toTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class UserRepository(
    private val userApiService: UserApiService,
    private val userDao: UserDao, // Inject the DAO
    private val context: Context,
    private val tagDao: TagDao,
    private val postDao: PostDao,
    private val folderApiService: FolderApiService
) : IUserRepository {

    private var currentUser: User? = null
    private var authToken: String? = null
    private val credentialsClient: CredentialsClient = Credentials.getClient(context)

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USERNAME = "KEY_USERNAME"
        private const val KEY_EMAIL = "KEY_EMAIL"
        private const val KEY_PASSWORD = "KEY_PASSWORD"
    }

    // Save user credentials to SharedPreferences
    private fun saveUserCredentials(user: User, password: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, user.username)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    // Retrieve saved user credentials
    fun getSavedUserCredentials(): UserCredentials? {
        val username = sharedPreferences.getString(KEY_USERNAME, null)
        val email = sharedPreferences.getString(KEY_EMAIL, null)
        val password = sharedPreferences.getString(KEY_PASSWORD, null)

        return if (username != null && email != null && password != null) {
            UserCredentials(username, password)
        } else {
            null
        }
    }

    // Inside UserRepository
    fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }


    suspend fun getCurrentUserFolders(): List<Folder> {
        return try {
            val userWithFolders = userDao.getUserByIdWithFolders(currentUser?.id ?: 0)
            val userFolders = userWithFolders?.userFolders
            Log.d("FLOW", "Fetched user folders from DB: $userFolders")
            if (userFolders.isNullOrEmpty()){
                val folders = folderApiService.getFoldersByUser()
                folders.forEach { folder -> folder.user = currentUser }
                Log.d("FLOW", "FOLDERS FROM API: $folders")
                userDao.insertFolders(folders.map { it.toFolderEntity() })
                folders
            } else {
                userFolders.map { it.toFolder() }
            }
        } catch (e : Exception){
                Log.e("FLOW", "Error fetching folders: ${e.message}", e)
                emptyList()
            }
        }


    suspend fun getFolderById(folderId: Long): Folder? {
        return try {
            val localFolder = userDao.getFolderById(folderId)
            if (localFolder != null) {
                val detailedFolder = userDao.getDetailedFolderById(folderId)
                val userInFolder = detailedFolder.folderEntity.userFolderId?.let {
                    userDao.getUserById(
                        it
                    )
                }
                val folder = detailedFolder.toFolder()
                val postsFromFolder =
                    postDao.getDetailedPostsById(detailedFolder.posts.map { it.postId })
                folder.posts = postsFromFolder.map { it.toPost() }.toMutableList()

                if (userInFolder != null) {
                    folder.user = userInFolder.toUser()
                }

                // full folder completed and returned
                folder
            } else {
                val apiFolder = folderApiService.getFolderById(folderId)
                apiFolder
            }
        } catch (e : Exception){
            Log.e("FLOW", "Error fetching folder with id: $folderId")
            null
        }
    }



    @Transaction
    suspend fun updateFolderWithPost(folder: Folder, post: Post): Folder?{
            return try {
                // check folder for that post in the posts
                if (!folder.posts?.contains(post)!!) {
                    folder.posts = (folder.posts ?: mutableListOf()).apply { add(post) }
                    val folderDTO = folder.toFolderDTO()
                    val newFolder = folderApiService.updateFolder(folder.id, folderDTO)
                    saveFolder(newFolder!!)
                }
                folder
            } catch (e: Exception) {
                Log.e("FLOW", "ERROR with updating folder with post")
                null
            }
    }

    private suspend fun saveFolder(folder: Folder) {
        withContext(Dispatchers.IO){
            try {
                val folderEntity = folder.toFolderEntity()
                userDao.insertFolder(folderEntity)

                val foldersWithPosts = folder.posts?.map { post ->
                    FolderWithPostsCrossRef(folder.id, post.id)
                }
                foldersWithPosts?.let { postDao.insertFoldersWithPosts(foldersWithPosts) }

                userDao.insertFolder(folderEntity)
            } catch (e : Exception){
                Log.e("FLOW", "error with saving new folder")
            }
        }
    }




    suspend fun createFolder(title: String, description: String): Folder? {
        return try {
            val folderDTO = FolderDTO(title, description, emptyList())
            val folder = folderApiService.createFolder(folderDTO)
            if (folder != null) {
                userDao.insertFolder(folder.toFolderEntity())
            }
            folder
        } catch (e: Exception) {
            Log.e("FLOW", "Error creating folder: ${e.message}", e)
            null
        }
    }


    // Get a specific user either from the API or fallback to the local database
    @Transaction
    suspend fun getUserAccount(id: Long): User? {
        return try {
            val user = userApiService.getUserAccount() // Fetch from API
            Log.d("FLOW", "Fetched user from API: $user")
            user?.let {
                // insert user in the database
                userDao.insertUser(UserEntity(it.id, it.username, it.email))
                //insert posts in the database
                it.posts?.let { posts ->
                    postDao.insertPosts(posts.map { post -> post.toPostEntity(user.id) })
                }

                // insert tags
                it.preferredTags?.let { tags -> tagDao.insertTags(tags.map { tag -> tag.toTagEntity() }) }
                it.preferredTags?.forEach{ tags -> userDao.insertUserWithTag(UserWithTagsCrossRef(user.id, tags.id)) }
            } // Save to DB new folders

            // return user
            user
        } catch (e: Exception) {
            Log.e("FLOW", "Error fetching user from API, loading from local DB", e)
            // Fallback to local database
            val userEntity = userDao.getUserById(id) ?: return null
            // get user tags from database
            val userTags = userDao.getUserByIdWithTags(id)
            val user = userEntity.toUser()
            user.preferredTags = userTags.tags.map { it.toTag() }
            // posts
            val userWithPosts = userDao.getUserByIdWithPosts(id)
            val postsWithDetails = postDao.getDetailedPostsById(userWithPosts.userPosts.map { it.postId })
            user.posts = postsWithDetails.map { it.toPost() }

            user
        }
    }

    override suspend fun login(username: String, password: String): User? {
        val credentials = UserCredentials(username, password)
        return try {
            Log.d("FLOW", "Logging in user: ${credentials.username} and ${credentials.password}")

            // Call the API to login
            val response = userApiService.login(credentials)
            Log.d("FLOW", "credentials from the api $credentials")

            // Extract the token from the response
            val token = response.token
            authToken = token
            saveAuthTokenToSharedPreferences(authToken!!)

            // Fetch the user details using the token
            val loggedInUser = userApiService.getUserByUsername()

            // Save the user credentials
            if (loggedInUser != null) {
                saveUserCredentials(loggedInUser, password)
                currentUser = loggedInUser // Cache the current user

                // Save the logged-in user to local database
                userDao.insertUser(UserEntity(loggedInUser.id, loggedInUser.username, loggedInUser.email))
            }
            loggedInUser
        } catch (e: HttpException) {
            if (e.code() == 403) {
                Log.e("FLOW", "403 Error: Access Denied. Redirecting to login.")
                clearSavedCredentials()
                return null
            }
            Log.e("FLOW", "HTTP Error: ${e.code()} - ${e.message()} - ${e.response()?.errorBody()?.string()}")
            return null
        } catch (e: Exception) {
            Log.e("FLOW", "Error logging in user", e)
            Log.d("FLOW", "ERROR : ${e.message}")
            null
        }
    }

    private fun saveAuthTokenToSharedPreferences(token: String) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    private fun clearSavedCredentials() {
        sharedPreferences.edit().clear().apply()
        credentialsClient.disableAutoSignIn()
    }
}
