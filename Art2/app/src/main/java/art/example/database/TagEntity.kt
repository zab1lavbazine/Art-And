package art.example.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import art.example.api.data.Tag


@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val id: Long,
    val name: String
)




fun Tag.toTagEntity(): TagEntity {
    return TagEntity(
        id = id,
        name = name
    )
}