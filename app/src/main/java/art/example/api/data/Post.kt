package art.example.api.data

import art.example.database.entities.PostEntity
import kotlinx.serialization.Serializable


@Serializable
data class Post(
    val id: Long,
    val title: String,
    val description: String,
    val tags: MutableList<Tag>,
    val image: Image? = null,
    val patron: User
)


fun Post.toPostEntity(): PostEntity {
    return PostEntity(
        postId = id,
        title = title,
        description = description,
        userId = patron.id
    )
}
