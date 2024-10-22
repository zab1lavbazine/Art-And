package art.example.database.TagDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import art.example.database.TagEntity


@Dao
interface TagDao {

    // Insert a single tag, using REPLACE strategy to handle conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)


    @Query("SELECT * FROM tags")
    suspend fun loadTags(): List<TagEntity>


}
