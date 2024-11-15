package art.example.api.data

import art.example.api.data.DTO.FolderDTO
import art.example.database.entities.FolderEntity

data class Folder(
    val id: Long,
    val title : String,
    val description: String,
    var user: User?,
    var posts: MutableList<Post>
)


fun Folder.toFolderEntity(): FolderEntity {
    return FolderEntity(
        folderId = this.id,
        title = this.title,
        description = this.description,
        userFolderId = this.user?.id ?: -1L // error if user id is -1L
    )
}
