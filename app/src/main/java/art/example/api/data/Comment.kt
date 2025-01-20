package art.example.api.data


data class Comment(
    val id: Long,
    val text: String,
    val authorUsername: String,
    val date: String
)