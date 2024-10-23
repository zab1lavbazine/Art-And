package art.example.database.PostDao

import androidx.room.*
import art.example.api.data.Post
import art.example.database.*

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts : List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostWithTags(postWithTags: List<PostWithTags>)

    // Fetch posts with their associated tags and images
    @Transaction
    @Query("SELECT * FROM posts")
    suspend fun getAllPostsWithDetails(): List<PostWithTagsAndImage>

    @Transaction
    @Query("SELECT * FROM posts WHERE postId IN (:posts)")
    suspend fun getDetailedPostsById(posts: List<Long>): List<PostWithTagsAndImage>


    @Transaction
    @Query("SELECT * FROM posts where postId = :postId")
    suspend fun getPostWithDetails(postId: Long): PostWithTagsAndImage?

    // Fetch a single post with associated tags and images
    @Transaction
    @Query("SELECT * FROM posts WHERE postId = :postId")
    suspend fun getPostWithTags(postId: Long): PostWithTagsAndImage?

    @Query("DELETE FROM posts" )
    suspend fun deleteAllPosts()
}
