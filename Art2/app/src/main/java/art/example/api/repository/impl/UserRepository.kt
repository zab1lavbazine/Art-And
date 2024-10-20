package art.example.api.repository.impl

import android.content.Context
import android.provider.Settings.Global.putString
import android.util.Log
import art.example.api.data.User
import art.example.api.data.toPostEntity
import art.example.api.data.toTagEntity
import art.example.api.reponses.UserCredentials
import art.example.api.repository.IUserApiService
import art.example.api.service.UserApiService
import art.example.database.UserDao
import art.example.database.UserEntity

class UserRepository(
    private val userApiService: UserApiService,
    private val userDao: UserDao, // Add UserDao dependency
    private val context: Context
) : IUserApiService {

    private var currentUser: User? = null
    private var authToken: String? = null

    private val staticUser = User(1, "adm", "admin@gmail.com")



    companion object {
        private const val KEY_USERNAME = "KEY_USERNAME"
        private const val KEY_EMAIL = "KEY_EMAIL"
        private const val KEY_PASSWORD = "KEY_PASSWORD"
    }

    override suspend fun getUsers(): List<User> {
        return try {
            // Fetch users from the database
            val userEntities = userDao.getAllUsers()
            userEntities.map { userEntity ->
                User(userEntity.id, userEntity.username, userEntity.email)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching users from database", e)
            emptyList() // Return an empty list on error
        }
    }

    override suspend fun getUserById(id: Long): User? {
        return try {
            // Fetch user from the database by ID
            val userEntity = userDao.getUserById(id) // Ensure you have this method in UserDao
            userEntity?.let {
                User(it.id, it.username, it.email)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user by ID from database", e)
            null // Return null on error
        }
    }

    // include that shared preferences
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private fun saveUserCredentials(user: User, password: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, user.username)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PASSWORD, password) // Save the password securely
            apply() // Commit changes asynchronously
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

    // Login method that sets the current user and auth token
    override suspend fun login(username: String, password: String): User? {
        val credentials = UserCredentials(username, password)
        return try {
            // Simulate an API response (in practice, call userApiService.login)
            val responseToken = "token" // Replace with actual response token
            // setAuthToken(responseToken) // Save the token after successful login

            // Fetch the user either from the database or the API
            val loggedInUser = staticUser

            // Save the user credentials
            saveUserCredentials(loggedInUser, password)

            // If the user is not found in the database, you might want to handle the login process differently
            // For example, you could perform the actual API login here
            if (loggedInUser == null) {
                // Handle the case where the user is not found after login
                Log.e("UserRepository", "User not found after login")
                return null
            }

            // Optionally, set the current user here if needed
            // setCurrentUser(loggedInUser)

            loggedInUser // Return the logged-in user
        } catch (e: Exception) {
            Log.e("UserRepository", "Error logging in user", e)
            null // Handle exceptions, e.g., show error message
        }
    }

    fun clearSavedCredentials() {
        sharedPreferences.edit().clear().apply()
    }

    // Get the current logged-in user
    fun getCurrentUser(): User? {
        return currentUser
    }

    // Get the current auth token
    fun getAuthToken(): String? {
        return authToken
    }

    // Helper function to find user by username
    private suspend fun getUserByUsername(username: String): User? {
        return try {
            // First, try to fetch the user from the database
            val userEntity = userDao.getUserByUsername(username) // Implement this DAO method

            // If the user is found in the database, return it
            userEntity?.let {
                // You may want to convert UserEntity back to User here if necessary
                return convertToUser(it)
            }

            // If not found, fetch the user from the API
            val userFromApi = userApiService.getUserByUsername(username)

            // Map the API User object to UserEntity
            userFromApi?.let { apiUser ->
                val newUserEntity = UserEntity(
                    id = apiUser.id,
                    username = apiUser.username,
                    email = apiUser.email
                )
                // Save the new userEntity in the database
                userDao.insertUser(newUserEntity)

                // Fetch and save user's posts and tags as well
                apiUser.posts?.forEach { post ->
                    userDao.insertPost(post.toPostEntity(newUserEntity.id)) // Use the newUserEntity.id
                }

                apiUser.preferredTags?.forEach { tag ->
                    userDao.insertTag(tag.toTagEntity(newUserEntity.id)) // Use the newUserEntity.id
                }

                // Return the mapped user object from API
                return apiUser // Adjust based on your return type needs
            }

            null // Return null if user is not found in both sources
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user by username", e)
            null // Handle exceptions gracefully
        }
    }

    // Helper function to convert UserEntity back to User if needed
    private fun convertToUser(userEntity: UserEntity): User {
        return User(
            id = userEntity.id,
            username = userEntity.username,
            email = userEntity.email,
            // Add any other required fields if applicable
        )
    }


}
