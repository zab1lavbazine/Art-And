package art.example.api.data

import art.example.database.entities.UserEntity
import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    var preferredTags: MutableList<Tag> = mutableListOf()
) {
    fun toUserEntity(): UserEntity {
        return UserEntity(
            userId = id,
            username = username,
            email = email
        )
    }
}



