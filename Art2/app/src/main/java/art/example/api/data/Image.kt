package art.example.api.data

import art.example.database.ImageEntity
import kotlinx.serialization.Serializable



@Serializable
data class Image(
    val id: Long,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (id != other.id) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}


fun Image.toImageEntity(postId: Long): ImageEntity {
    return ImageEntity(
        id = this.id,
        data = data,
        postId = postId  // Associate with the Post ID
    )
}