package art.example.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import art.example.api.data.Tag


@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val tagId: Long,
    val name: String
)


fun TagEntity.toTag(): Tag{
    return Tag (
        id = tagId,
        name = name
    )
}




