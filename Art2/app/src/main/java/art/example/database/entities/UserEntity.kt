package art.example.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import art.example.api.data.Folder
import art.example.api.data.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: Long,
    val username: String,
    val email: String
){
    fun toUser(): User {
        return User(
            id = userId,
            username = username,
            email = email
        )
    }
}

// many to many relation with TAGS

@Entity(primaryKeys = ["userId", "tagId"])
data class UserWithTagsCrossRef(
    val userId : Long,
    val tagId : Long
)

data class UserWithTags(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "tagId",
        associateBy = Junction(UserWithTagsCrossRef::class)
    )
    val tags: List<TagEntity>
)

// one to many relation to POSTS


data class UserWithPosts(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val userPosts: List<PostEntity>
)


// one to many relation to FOLDER


data class UserWithFolders(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userFolderId"
    )
    val userFolders: List<FolderEntity>
)
