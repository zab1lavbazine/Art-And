package art.example.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "tags", foreignKeys = [
    ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE // Optional
    )
])
data class TagEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val userId: Long // Foreign key referencing UserEntity
)