package art.example.database

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithTags(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val preferredTags: List<TagEntity>
)