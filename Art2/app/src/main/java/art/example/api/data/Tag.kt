package art.example.api.data

import art.example.database.TagEntity
import kotlinx.serialization.Serializable


@Serializable
data class Tag(
    val id: Long,
    val name: String
)


//to tag
fun Tag.toTagEntity(userId: Long): TagEntity {
    return TagEntity(
        id = id,
        name = name,
        userId = userId
    )
}
