package art.example.database.PostDao

import androidx.room.*
import art.example.database.*

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostWithTags(postWithTags: List<PostWithTags>)

    // Fetch posts with their associated tags and images
    @Transaction
    @Query("SELECT * FROM posts")
    suspend fun getAllPostsWithDetails(): List<PostWithTagsAndImage>

    // Fetch a single post with associated tags and images
    @Transaction
    @Query("SELECT * FROM posts WHERE postId = :postId")
    suspend fun getPostWithTags(postId: Long): PostWithTagsAndImage?

    @Query("DELETE FROM posts" )
    suspend fun deleteAllPosts()
}
