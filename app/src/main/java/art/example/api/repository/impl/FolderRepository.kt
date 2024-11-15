package art.example.api.repository.impl


import android.util.Log
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import art.example.api.data.DTO.FolderDTO
import art.example.api.data.Folder
import art.example.api.data.Post
import art.example.api.data.User
import art.example.api.data.toFolderEntity
import art.example.api.service.FolderApiService
import art.example.database.entities.FolderEntity
import art.example.database.entities.FolderWithPosts
import art.example.database.entities.PostEntity
import art.example.database.folderDao.FolderDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderRepository(
    private val folderDao: FolderDao,
    private val folderApiService: FolderApiService
) {


    suspend fun getFoldersByUserId(userId:Long): List<Folder>{
        try {
            val user = getUser(userId)
            val foldersApi = folderApiService.getFoldersByUser()
            if (foldersApi.isNotEmpty()) {
                foldersApi.forEach{
                    it.user = user
                }
                Log.d("FLOW", "getting folders from api : $foldersApi")
                saveFolders(foldersApi)
            }
            return foldersApi
        } catch (e: Exception){
            Log.d("FLOW", "Error getting folders by user id : $userId")
            Log.d("FLOW", "Error e: $e")
            return emptyList()
        }
    }

    suspend fun deleteFolderById(folderId: Long){
        folderDao.deleteFolderById(folderId)
        folderApiService.deleteFolderById(folderId)
        Log.d("FLOW", "folder deleted by id: $folderId")
    }


    suspend fun mapLocalFolderData(folderWithPosts: FolderWithPosts):Folder? {
        try {
            val user = getUser(folderWithPosts.folderEntity.userFolderId)
            Log.d("FLOW", "User from repo: $user")
            val folder = Folder(
                id = folderWithPosts.folderEntity.folderId,
                title = folderWithPosts.folderEntity.title,
                description = folderWithPosts.folderEntity.description,
                posts = mapPost(folderWithPosts.posts, user),
                user = user
            )
            Log.d("FLOW", "Folder $folder")
            return folder
        } catch (e: Exception){
            Log.d("FLOW", "Error while mapping folder: ${e.message}")
        }
        return null
    }


    suspend fun updateFolderInfo(id: Long, title: String, description: String, currentUserId: Long): Folder? {
        val localFolder = folderDao.getDetailedFolderById(id)
        if (localFolder != null) {
            val postsIds = localFolder.posts.map { it.postId }
            val folderDTO = FolderDTO(
                title = title,
                description = description,
                postIds = postsIds
            )
            val newFolder = folderApiService.updateFolderById(id, folderDTO)
            if (newFolder != null) {
                val user = getUser(currentUserId)
                Log.d("FLOW", "Updated folder from api: $newFolder")
                newFolder.user = user
                updateFolder(newFolder)
            }
            return newFolder
        }
        throw Exception("Error updating folder info")
    }


    suspend fun deletePostFromFolder(post: Post, folder: Folder, currentUserId: Long): Folder? {
        val postIds = folder.posts.filter{ it.id != post.id}.map { it.id }
        val folderDTO = FolderDTO(
            title = folder.title,
            description = folder.description,
            postIds = postIds
        )
        Log.d("FLOW", "Folder dto: $folderDTO")
        val newFolder = folderApiService.updateFolderById(folderId = folder.id, folderDTO)
        if (newFolder != null) {
            Log.d("FLOW", "Saving after deleting post from folder: $newFolder")
            val user = getUser(currentUserId)
            newFolder.user = user
            updateFoldersWithPosts(listOf(newFolder))
        }
        return newFolder
    }


    suspend fun getUser(userId: Long): User{
        val user = folderDao.getUser(userId) ?: throw Exception("Error getting user with id: $userId")
        return User(
            id = user.userId,
            username = user.username,
            email = user.email
        )
    }

    suspend fun mapPost(folderPosts: List<PostEntity>, user: User): MutableList<Post> {
        val detailedPosts = folderDao.getDetailedPosts(folderPosts.map { it.postId })
        val posts = detailedPosts.map {
            Post(
                id = it.post.postId,
                title = it.post.title,
                description = it.post.description,
                image = it.images.toImage(),
                patron = user,
                tags = mutableListOf()
            )
        }.toMutableList()
        return posts
    }


    suspend fun mapLocalFolderData(folderEntities: List<FolderEntity>, user: User): List<Folder>{
        val folders = folderEntities.map { folderD ->
            Folder(
                id = folderD.folderId,
                title = folderD.description,
                description = folderD.description,
                posts = mutableListOf(),
                user = user
            )
        }
        return folders
    }


    suspend fun createFolderForUser(title: String, description: String, currentUserId: Long){
        val newFolder = FolderDTO(
            title = title,
            description = description,
            postIds = emptyList()
        )

        val folder = folderApiService.createFolder(folderDTO = newFolder)
        if (folder != null) {
            val user = getUser(currentUserId)
            folder.user = user
            Log.d("FLOW", "Saving new folder: $folder")
//            saveFolders(listOf(folder))
        }
    }

    suspend fun getDetailedFolderById(folderId: Long): Folder?{
        try {
            val folderDat =  folderApiService.getFolderById(folderId)
            if (folderDat != null){
                return folderDat
            }
            Log.d("FLOW", "Detailed folder is null in the api")
            return null
        } catch (e : Exception){
            Log.d("FLOW", "Error getting folder by id: $folderId")
            return null
        }
    }


    suspend fun savePostInFolder(post: Post, folder: Folder, currentUserId: Long): Folder? {
        try {
            val getApiFolder = folderApiService.getFolderById(folder.id)
            if (getApiFolder!= null){
                if (!getApiFolder.posts.any{ it.id == post.id}){
                    val newPostsIds = getApiFolder.posts.map { it.id } + post.id
                    val folderDTO = FolderDTO(
                        title = getApiFolder.title,
                        description = getApiFolder.description,
                        postIds = newPostsIds
                    )
                    val newFolder = folderApiService.updateFolderById( folder.id, folderDTO)
                    if(newFolder != null){
                        Log.d("FLOW", "SAVE POST IN FOLDER: $newFolder")
                        newFolder.user = getUser(currentUserId)
                        updateFoldersWithPosts(listOf(newFolder))
                    }
                    return newFolder
                }
                return folder
            }
            return null
        } catch (e: Exception){
            Log.d("FLOW", "Error saving post in folder")
            return null
        }
    }

    suspend fun updateFoldersWithPosts(folders: List<Folder>){
        withContext(Dispatchers.IO){
            try {
                folders.forEach { folder ->
                    folderDao.updateFolderWithPosts(folder)
                }
                Log.d("FLOW", "Updating folders $folders")
            } catch (e: Exception) {
                Log.d("FLOW", "ERROR updating folders with posts: $folders, exception: $e")
            }
        }
    }


    suspend fun updateFolder(folder: Folder){
        withContext(Dispatchers.IO){
            try {
                folderDao.updateFolder(folder.toFolderEntity())
                Log.d("FLOW", "Updating folders $folder")
            } catch (e: Exception) {
                Log.d("FLOW", "ERROR updating folders: $folder, exception: $e")
            }
        }
    }


    suspend fun saveFolders(folders : List<Folder>){
        withContext(Dispatchers.IO ) {
            try {
                folderDao.insertFolders(folders)
                Log.d("FLOW", "Saving folders $folders")
            } catch (e: Exception) {
                Log.d("FLOW", "ERROR saving folders: $folders, exception: $e")
            }
        }
    }


}