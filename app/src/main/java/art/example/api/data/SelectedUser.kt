package art.example.api.data

import kotlinx.serialization.Serializable


@Serializable
data class SelectedUser(
    val id: Long,
    val username: String,
    val email: String,
    val posts: MutableList<Post> = mutableListOf()
)