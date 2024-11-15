package art.example.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import art.example.api.data.Post


@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val postId: Long,
    val userId: Long,
    val title: String,
    val description: String,
)


@Entity(primaryKeys = ["tagId", "postId"])
data class PostWithTags(
    val tagId: Long,
    val postId: Long
)


data class PostWithTagsAndImage(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "postId",
        entityColumn = "id"
    )
    val images: ImageEntity,
    @Relation(
        parentColumn = "postId",
        entityColumn = "tagId",
        associateBy = Junction(PostWithTags::class)
    )
    val tags: List<TagEntity>
)



