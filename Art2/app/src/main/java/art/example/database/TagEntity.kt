package art.example.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import art.example.api.data.Tag


@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val tagId: Long,
    val userTagId: Long? = null,
    val name: String
)


fun TagEntity.toTag(): Tag{
    return Tag (
        id = tagId,
        name = name
    )
}




