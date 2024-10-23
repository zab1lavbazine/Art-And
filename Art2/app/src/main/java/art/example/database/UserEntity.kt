package art.example.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import art.example.api.data.Folder
import art.example.api.data.Post
import art.example.api.data.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: Long,
    val username: String,
    val email: String
)

fun UserEntity.toUser(): User{
    return User(
        id = userId,
        username = username,
        email = email
    )
}





@Entity
data class UserWithTags(
    @Embedded val user : UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userTagId"
    )
    val userTags: List<TagEntity>
)

fun UserWithTags.toUser() : User {
    return User(
        id = user.userId,
        username = user.username,
        email = user.email,
        preferredTags = userTags.map { it.toTag() }
    )
}


@Entity
data class UserWithPosts(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userPostId"
    )
    val userPosts: List<PostEntity>
)


@Entity
data class UserWithFolders(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userFolderId"
    )
    val userFolders: List<FolderEntity>
){
    fun toFolder(): Folder {
        return Folder(
            id = userFolders.first().folderId,
            title = userFolders.first().title,
            description = userFolders.first().description
        )
    }

}
