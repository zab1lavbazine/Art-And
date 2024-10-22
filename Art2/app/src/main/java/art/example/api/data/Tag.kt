package art.example.api.data

import art.example.database.TagEntity
import kotlinx.serialization.Serializable


@Serializable
data class Tag(
    val id: Long,
    val name: String
)


//to tag
fun Tag.toTagEntity(): TagEntity {
    return TagEntity(
        tagId = id,
        name = name
    )
}
