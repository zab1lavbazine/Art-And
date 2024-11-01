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
    val images: List<ImageEntity>,
    @Relation(
        parentColumn = "postId",
        entityColumn = "tagId",
        associateBy = Junction(PostWithTags::class)
    )
    val tags: List<TagEntity>
) {
    // Convert to domain model Post
    fun toPost(): Post {
        return Post(
            id = post.postId,
            title = post.title,
            description = post.description,
            tags = tags.map { it.toTag() }, // Map TagEntity to Tag
            image = images.firstOrNull()?.toImage() // Assuming only one image per post
        )
    }
}




