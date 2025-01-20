package art.example.api.data.DTO

import art.example.api.data.Comment
import art.example.api.data.Image
import art.example.api.data.Tag
import art.example.api.data.User

data class DetailedPost(
    val id: Long,
    val title: String,
    val description: String,
    val comments: MutableList<Comment>,
    val tags: MutableList<Tag>,
    val image: Image? = null,
    val patron: User
)
