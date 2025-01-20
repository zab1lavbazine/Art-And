package art.example.api.repository.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import art.example.api.data.Comment
import art.example.api.data.DTO.PostDTO
import art.example.api.data.Post
import art.example.api.repository.IPostRepository
import art.example.api.service.PostApiService
import art.example.api.service.ResponseItem
import art.example.database.PostDao.PostDao
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class PostRepository(
    private val postApiService: PostApiService, // API Service
    private val postDao: PostDao, // Post DAO
) : IPostRepository {




    suspend fun getPosts(pageSize: Int, pageNumber: Int): ResponseItem<Post> {
        return try {
            // Fetch posts from the API for the specified page number
            val response = postApiService.getPosts(pageNumber, pageSize)
            Log.d("FLOW", "Response from the api: $response")
            response
        }catch (e: Exception){
            Log.d("FLOW", "ERROR getting posts")
            ResponseItem(emptyList())
        }
    }

    suspend fun searchPosts(query : String, pageSize: Int, pageNumber: Int) : ResponseItem<Post>{
        return try {
            val response = postApiService.searchPosts(query, pageNumber, pageSize)
            Log.d("FLOW", "Founded posts: ${response.content}")
            response
        } catch (e: Exception){
            Log.d("FLOW", "Error searching for posts")
            ResponseItem(emptyList())
        }
    }

    suspend fun deletePostById(postId: Long){
        postApiService.deletePostById(postId)
    }


    suspend fun getPostsByUserId(userId: Long): List<Post>{

        val fetchedPosts = postApiService.getPostsByUserId(userId)
        if (fetchedPosts.isNotEmpty()){
            return fetchedPosts
        }
        return emptyList()
    }


    suspend fun updatePost(context: Context, post: Post): Post? {
        // Convert the image URI to MultipartBody.Part if image exists
        val filePart = post.image?.file?.let { getMultipartFromUri(context, it) }

        // Create PostDTO with the image file (MultipartBody.Part)
        val postDTO = PostDTO(
            title = post.title,
            description = post.description,
            tagsId = post.tags.map { it.id },
            file = filePart
        )

        // RequestBody for title, description, and tagsId
        val title = RequestBody.create("text/plain".toMediaTypeOrNull(), postDTO.title ?: "")
        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), postDTO.description ?: "")
        val tagsId = RequestBody.create("application/json".toMediaTypeOrNull(), postDTO.tagsId.joinToString(","))

        Log.d("FLOW", "Updating post with postDTO: $postDTO")

        // Call the API to update the post, passing the image as part of the request
        val updatedPost = postApiService.updatePostById(post.id, title, description, tagsId, postDTO.file)

        // Check if the post was updated successfully
        if (updatedPost != null) {
            Log.d("FLOW", "Post updated successfully: $updatedPost")
            return updatedPost
        }

        return null
    }



    override suspend fun getPostById(id: Long): Post? {

        val postApi = postApiService.getPostById(id)

        return postApi
    }

    suspend fun getPostCommentsByPostId(postId: Long): List<Comment>{
        val comments = postApiService.getPostCommentsByPostId(postId)
        return comments
    }

    suspend fun postNewCommentUnderPostWithId( postId: Long, comment: String): Comment?{
        val newComment = postApiService.postNewCommentUnderPostWithId( postId, comment)
        return newComment
    }

    suspend fun deleteCommentByIdFromPost(postId: Long, commentId: Long) {
        Log.d("FLOW", "deleting comment : $commentId, from post: $postId")
        postApiService.deleteCommentByIdFromPost(postId, commentId)
    }

    suspend fun createPost(postDTO: PostDTO, imageBitmap: Bitmap): Post? {
            try {
                // Convert Bitmap to MultipartBody.Part
                val filePart = getMultipartFromBitmap(imageBitmap)
                if (filePart != null) {
                    // Create RequestBody for title, description, and tags
                    val title = RequestBody.create("text/plain".toMediaTypeOrNull(), postDTO.title ?: "")
                    val description = RequestBody.create("text/plain".toMediaTypeOrNull(), postDTO.description ?: "")
                    val tagsId = RequestBody.create("application/json".toMediaTypeOrNull(), postDTO.tagsId.joinToString(","))

                    // Send the request with the PostDTO and the image file
                  return postApiService.createPost(title, description, tagsId, filePart)
                }
                return null
            } catch (e: Exception) {
                Log.e("FLOW", "Error creating post $e")
                return null
            }
    }


    private suspend fun getMultipartFromUri(context: Context, uri: String): MultipartBody.Part? {
        return withContext(Dispatchers.IO) {
            try {
                // Step 1: Convert URI to Bitmap
                val bitmap = getBitmapFromUri(context, uri) ?: return@withContext null

                // Step 2: Convert Bitmap to ByteArray
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                // Step 3: Create RequestBody
                val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)

                // Step 4: Create MultipartBody.Part
                return@withContext MultipartBody.Part.createFormData("file", "image.jpg", requestBody)
            } catch (e: Exception) {
                Log.d("FLOW", "Error converting URI to multipart: $e")
                e.printStackTrace()
                null
            }
        }
    }


    private suspend fun getBitmapFromUri(context: Context, uri: String): Bitmap? {
        return try {
            if (uri.startsWith("http")) {
                // Handle remote image URL using Glide
                withContext(Dispatchers.IO) {
                    Glide.with(context)
                        .asBitmap()
                        .load(uri)
                        .submit()
                        .get()
                }
            } else {
                // Handle local file URI as before
                val inputStream = context.contentResolver.openInputStream(Uri.parse(uri))
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.e("FLOW", "Error getting Bitmap from URI: $e")
            null
        }
    }




    private suspend fun getMultipartFromBitmap(bitmap: Bitmap): MultipartBody.Part? {
        return withContext(Dispatchers.IO) {
            try {
                // Convert Bitmap to ByteArray as JPEG
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream) // Compress as JPEG
                val byteArray = byteArrayOutputStream.toByteArray()

                // Create RequestBody from byte array with MIME type "image/jpeg"
                val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)

                // Create MultipartBody.Part with .jpg extension
                return@withContext MultipartBody.Part.createFormData("file", "image.jpg", requestBody) // Use .jpg extension
            } catch (e: Exception) {
                Log.d("FLOW", "Error converting bitmap to multipart: $e")
                e.printStackTrace()
                null
            }
        }
    }


}
