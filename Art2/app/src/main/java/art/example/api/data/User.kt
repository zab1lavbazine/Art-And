package art.example.api.data

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val preferredTags: List<Tag>? = null,
    val posts: List<Post>? = null
)



