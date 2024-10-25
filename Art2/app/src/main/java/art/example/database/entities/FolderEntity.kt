package art.example.database.entities

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
fun FolderEntity.toFolder(): Folder {
    return Folder(
        id = this.folderId,
        title = this.title,
        description = this.description
    )
}






@Entity(primaryKeys = ["folderId", "postId"])
data class FolderWithPostsCrossRef (
    val folderId: Long,
    val postId: Long
)


data class FolderWithPosts (
    @Embedded val folderEntity: FolderEntity,
    @Relation(
        parentColumn = "folderId",
        entityColumn = "postId",
        associateBy = Junction(FolderWithPostsCrossRef::class)
    )
    val posts: List<PostEntity>
)