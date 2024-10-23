package art.example.api.data

import art.example.database.FolderEntity

data class Folder(
    val id: Long,
    val title : String,
    val description: String,
    var user: User? = null,
    val posts: List<Post>? = null
)



fun Folder.toFolderEntity(): FolderEntity {
    return FolderEntity(
        folderId = this.id,
        title = this.title,
        description = this.description,
        userFolderId = this.user?.id
    )
}
