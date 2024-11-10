package art.example.database.folderDao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import art.example.api.data.Folder
import art.example.api.data.Image
import art.example.api.data.Post
import art.example.api.data.Tag
import art.example.api.data.toFolderEntity
import art.example.api.data.toImageEntity
import art.example.api.data.toPostEntity
import art.example.api.data.toTagEntity
import art.example.database.entities.FolderEntity
import art.example.database.entities.FolderWithPosts
import art.example.database.entities.FolderWithPostsCrossRef
import art.example.database.entities.ImageEntity
import art.example.database.entities.PostEntity
import art.example.database.entities.PostWithTags
import art.example.database.entities.PostWithTagsAndImage
import art.example.database.entities.TagEntity
import art.example.database.entities.UserEntity


@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insertFolder(folder: FolderEntity)

    @Transaction
    suspend fun insertFolders(folders: List<Folder>){
        folders.forEach{ folder ->
            Log.d("FLOW", "SAVING FOLDER: $folder")
            insertFolder(folder.toFolderEntity())
            insertPosts(folder.posts)
            insertFolderWithPosts(folder)
        }
    }


    @Transaction
    @Query("SELECT * FROM posts WHERE postId in (:posts)")
    suspend fun getDetailedPosts(posts: List<Long>) : List<PostWithTagsAndImage>

    @Query("SELECT * FROM users WHERE  userId = :userId")
    suspend fun getUser(userId: Long): UserEntity?

    @Transaction
    suspend fun insertFolderWithPosts(folder: Folder){
        folder.posts.forEach { post ->
            insertFolderWithPost(FolderWithPostsCrossRef(folderId = folder.id, postId = post.id))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolderWithPost(folderWithPost: FolderWithPostsCrossRef)

    @Transaction
    suspend fun insertPosts(posts: MutableList<Post>){
        posts.forEach{
            post -> insertPost(post.toPostEntity())
            insertImage( post.image?.toImageEntity(post.id) ?: ImageEntity(id = -1L, postId = post.id, data = null) )
            insertTags(post.tags)
            insertTagsWithPost(post)
        }
    }


    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Transaction
    suspend fun insertTagsWithPost(post: Post){
        post.tags.forEach { tag ->
            insertTagWithPost(PostWithTags(tagId = tag.id, postId = post.id))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTagWithPost(tagWithPost: PostWithTags)

    @Transaction
    suspend fun insertTags(tags: MutableList<Tag>){
        tags.forEach { tag ->
            insertTag(tag.toTagEntity())
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)



    @Query("SELECT * FROM folders WHERE folderId = :folderId")
    suspend fun getFolderById(folderId: Long) : FolderEntity?

    @Query("SELECT * FROM folders WHERE userFolderId = :userId")
    suspend fun getFoldersByUserId(userId: Long) : List<FolderEntity>

    @Query("SELECT * FROM folders WHERE userFolderId =:userId")
    suspend fun getFolderByUserId(userId: Long): FolderEntity?

    @Query("SELECT * FROM folders WHERE folderId = :folderId")
    suspend fun getDetailedFolderById(folderId: Long): FolderWithPosts?


    @Transaction
    suspend fun deleteFolderWithConnections(folderId: Long) {
        // Delete connections (cross-references) in FolderWithPostsCrossRef
        deleteFolderConnections(folderId)

        // Delete the folder itself from the folders table
        deleteFolderById(folderId)
    }

    @Query("DELETE FROM folders WHERE folderId = :folderId")
    suspend fun deleteFolderById(folderId: Long)

    @Query("DELETE FROM FolderWithPostsCrossRef WHERE folderId = :folderId")
    suspend fun deleteFolderConnections(folderId: Long)



    @Transaction
    suspend fun updateFolderWithPosts(folder: Folder) {
        // Step 1: Insert or update the folder itself
        insertFolder(folder.toFolderEntity())

        // Step 2: Fetch existing posts for the folder with both folderId and postId
        val existingPosts = getFolderPosts(folder.id)
        if (folder.posts.isNullOrEmpty()){
            folder.posts = mutableListOf()
        }
        val newPosts = folder.posts.map { it.id }

        // Step 3: Find posts to delete (those in existingPosts but not in newPosts)
        val postsToDelete = existingPosts.filterNot { newPosts.contains(it.postId) }.map { it.postId }
        deletePostsByIds(postsToDelete)

        // Step 4: Insert or update the remaining posts and their connections
        insertPosts(folder.posts)
        insertFolderWithPosts(folder)
    }

    @Query("SELECT * FROM FolderWithPostsCrossRef WHERE folderId = :folderId")
    suspend fun getFolderPosts(folderId: Long): List<FolderWithPostsCrossRef>

    @Query("DELETE FROM posts WHERE postId IN (:postIds)")
    suspend fun deletePostsByIds(postIds: List<Long>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFolder(folder: FolderEntity)



}