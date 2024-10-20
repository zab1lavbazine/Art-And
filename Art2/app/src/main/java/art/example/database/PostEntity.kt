package art.example.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "posts", foreignKeys = [
    ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE // Optional: specify what happens on user deletion
    )
])
data class PostEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String,
    val userId: Long // Foreign key referencing UserEntity
)