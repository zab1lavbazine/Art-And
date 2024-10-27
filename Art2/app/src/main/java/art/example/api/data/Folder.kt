package art.example.api.data

import art.example.api.data.DTO.FolderDTO
import art.example.database.entities.FolderEntity

data class Folder(
    val id: Long,
    val title : String,
    val description: String,
    var user: User? = null,
    var posts: MutableList<Post>? = null
) {
    fun toFolderDTO(): FolderDTO {
        return FolderDTO(
            title = title,
            description = description,
            postIds = posts?.map { it.id } ?: emptyList()
        )
    }
}


fun Folder.toFolderEntity(): FolderEntity {
    return FolderEntity(
        folderId = this.id,
        title = this.title,
        description = this.description,
        userFolderId = this.user?.id
    )
}
