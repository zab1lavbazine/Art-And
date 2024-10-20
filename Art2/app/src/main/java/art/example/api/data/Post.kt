package art.example.api.data

import art.example.database.PostEntity
import kotlinx.serialization.Serializable


@Serializable
data class Post(
    val id: Long,
    val title: String,
    val description: String,
    val tags: List<Tag>? = null,
    val image: Image? = null,
    val imageUrl: String? = null,
    val patron: User? = null
)


fun Post.toPostEntity(userId: Long): PostEntity {
    return PostEntity(
        id = id,
        title = title,
        description = description,
        userId = userId
    )
}
