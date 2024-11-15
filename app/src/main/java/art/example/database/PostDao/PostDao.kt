package art.example.database.PostDao

import androidx.room.*
import art.example.database.entities.FolderWithPosts
import art.example.database.entities.FolderWithPostsCrossRef
import art.example.database.entities.ImageEntity
import art.example.database.entities.PostEntity
import art.example.database.entities.PostWithTags
import art.example.database.entities.PostWithTagsAndImage

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts : List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostsWithTags(postWithTags: List<PostWithTags>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostWithTag(postWithTag: PostWithTags)

    // Fetch posts with their associated tags and images
    @Transaction
    @Query("SELECT * FROM posts")
    suspend fun getAllPostsWithDetails(): List<PostWithTagsAndImage>

    @Transaction
    @Query("SELECT * FROM posts WHERE postId IN (:posts)")
    suspend fun getDetailedPostsById(posts: List<Long>): MutableList<PostWithTagsAndImage>


    @Transaction
    @Query("SELECT * FROM posts WHERE userId = :userId")
    suspend fun getDetailedPostsByUserId(userId: Long): MutableList<PostWithTagsAndImage>


    @Transaction
    @Query("SELECT * FROM posts where postId = :postId")
    suspend fun getPostWithDetails(postId: Long): PostWithTagsAndImage?

    // Fetch a single post with associated tags and images
    @Transaction
    @Query("SELECT * FROM posts WHERE postId = :postId")
    suspend fun getPostWithTagsAndImage(postId: Long): PostWithTagsAndImage?

    @Query("DELETE FROM posts" )
    suspend fun deleteAllPosts()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolderWithPost(folderWithPost: FolderWithPostsCrossRef)

    @Transaction
    @Query("DELETE FROM FolderWithPostsCrossRef WHERE folderId = :folderId AND postId = :postId")
    suspend fun deletePostWithFolder(folderId: Long, postId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoldersWithPosts(foldersWithPosts : List<FolderWithPostsCrossRef>)
}
