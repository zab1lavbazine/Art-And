package art.example.api.data.DTO

import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class InMemoryMultipartFile(
    private val name: String,
    private val content: ByteArray
) : MultipartFile {

    override fun getName(): String {
        return name
    }

    override fun getOriginalFilename(): String? {
        return name
    }

    override fun getContentType(): String? {
        return "image/jpeg" // or derive it based on the file type
    }

    override fun isEmpty(): Boolean {
        return content.isEmpty()
    }

    override fun getSize(): Long {
        return content.size.toLong()
    }

    override fun getBytes(): ByteArray {
        return content
    }

    override fun getInputStream(): InputStream {
        return ByteArrayInputStream(content)
    }

    override fun transferTo(dest: File) {
        throw UnsupportedOperationException("Not supported")
    }
}
interface MultipartFile {
    fun getName(): String
    fun getOriginalFilename(): String?
    fun getContentType(): String?
    fun isEmpty(): Boolean
    fun getSize(): Long
    fun getBytes(): ByteArray
    fun getInputStream(): InputStream
    fun transferTo(dest: File)
}
