package art.example.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import art.example.api.data.Folder


@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey val folderId: Long,
    val title: String,
    val userFolderId: Long? = null,
    val description: String
)


@Entity(primaryKeys = ["folderId", "postId"])
data class FolderWithPosts(
    val folderId: Long,
    val postId: Long
)



data class FolderWithPostsAndPostDetails(
    @Embedded val folder: FolderEntity,
    @Relation(
        parentColumn = "folderId",
        entityColumn = "postId",
        associateBy = Junction(FolderWithPosts::class)
    )
    val postsWithDetails: List<PostEntity> // This now includes the PostWithTagsAndImage
) {
    fun toFolder(): Folder {
        return Folder(
            id = folder.folderId,
            title = folder.title,
            description = folder.description
        )
    }
}




fun FolderEntity.toFolder(): Folder {
    return Folder(
        id = this.folderId,
        title = this.title,
        description = this.description
    )
}