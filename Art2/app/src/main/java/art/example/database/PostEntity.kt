package art.example.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import art.example.api.data.Post
import art.example.api.data.Tag


@Entity(
    tableName = "posts", foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PostEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String,
    val userId: Long // Foreign key referencing UserEntity
)

@Entity(
    primaryKeys = ["postId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PostTagCrossRef(
    val postId: Long,
    val tagId: Long
)


data class PostWithTags(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(PostTagCrossRef::class)
    )
    val tags: List<TagEntity>
)

fun PostWithTags.toPost(): Post {
    return Post(
        id = post.id,
        title = post.title,
        description = post.description,
        tags = tags.map { tag -> Tag(id = tag.id, name = tag.name) }
    )
}


fun Post.toPostEntityWithTags(userId: Long): Pair<PostEntity, List<TagEntity>> {
    val postEntity = PostEntity(
        id = id,
        title = title,
        description = description,
        userId = userId
    )

    val tagEntities = tags?.map { tag ->
        TagEntity(id = tag.id, name = tag.name)
    } ?: emptyList()

    return Pair(postEntity, tagEntities)
}







