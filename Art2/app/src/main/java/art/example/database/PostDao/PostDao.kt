package art.example.database.PostDao

import androidx.room.*
import art.example.database.*

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostTagCrossRef(crossRefs: List<PostTagCrossRef>)

    @Transaction
    suspend fun insertPostWithTags(post: PostEntity, tags: List<TagEntity>) {
        insertPost(post)
        insertTags(tags)
        val crossRefs = tags.map { PostTagCrossRef(post.id, it.id) }
        insertPostTagCrossRef(crossRefs)
    }

    @Transaction
    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostWithTags(postId: Long): PostWithTags

    // Get all posts with tags, images, and other details
    @Transaction
    @Query("SELECT * FROM posts")
    suspend fun getAllPostsWithDetails(): List<PostWithTags>

    //insert images
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>)
}
