package art.example.database.TagDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import art.example.database.PostTagCrossRef
import art.example.database.TagEntity


@Dao
interface TagDao {

    // Insert a single tag, using REPLACE strategy to handle conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    // Check if a tag already exists in the database
    @Query("SELECT COUNT(*) > 0 FROM tags WHERE id = :tagId")
    suspend fun doesTagExist(tagId: Long): Boolean

    // Insert the cross-reference between PostEntity and TagEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostTagCrossRef(crossRef: PostTagCrossRef)

    // Insert tags for a post, ensuring each tag exists and cross-reference is made
    @Transaction
    suspend fun insertTagsForPost(postId: Long, tags: List<TagEntity>) {
        for (tag in tags) {
            if (!doesTagExist(tag.id)) {
                insertTag(tag) // Insert the tag only if it does not exist
            }
            insertPostTagCrossRef(PostTagCrossRef(postId, tag.id)) // Link the tag to the post
        }
    }

    // Optionally, bulk insert method for better performance
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)
}
