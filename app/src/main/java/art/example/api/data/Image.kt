package art.example.api.data

import art.example.database.entities.ImageEntity
import kotlinx.serialization.Serializable


@Serializable
data class Image(
    val id: Long,
    val file: String?,

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (id != other.id) return false
        if (!file.contentEquals(other.file)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + file.hashCode()
        return result
    }
}


fun Image.toImageEntity(postId: Long): ImageEntity {
    return ImageEntity(
        id = this.id,
        data = file,
        postId = postId  // Associate with the Post ID
    )
}