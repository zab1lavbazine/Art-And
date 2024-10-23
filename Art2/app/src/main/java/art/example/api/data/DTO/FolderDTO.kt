package art.example.api.data.DTO

data class FolderDTO(
    val title: String,
    val description: String,
    val postIds: List<Long>
)
