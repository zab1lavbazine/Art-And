package art.example.api.repository.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import art.example.api.data.*
import art.example.api.data.DTO.InMemoryMultipartFile
import art.example.api.data.DTO.MultipartFile
import art.example.api.data.DTO.PostDTO
import art.example.api.repository.IPostApiService
import art.example.api.service.PostApiService
import art.example.database.PostDao.PostDao
import art.example.database.TagDao.TagDao
import art.example.database.UserDao.UserDao
import art.example.database.entities.PostWithTags
import art.example.database.entities.toTag
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class PostRepository(
    private val postApiService: PostApiService, // API Service
    private val postDao: PostDao, // Post DAO
    private val tagDao: TagDao, // Tag DAO
    private val userDao: UserDao,
    private val context : Context
) : IPostApiService {

    // In-memory cache
    private val postsCache = mutableListOf<Post>()

    // Fetch all posts (from cache, database, or API)
    override suspend fun getPosts(): List<Post> {
        return try {
            if (postsCache.isEmpty()) {
                // Fetch from local database
                val localPosts = withContext(Dispatchers.IO) { postDao.getAllPostsWithDetails() }
                Log.d("FLOW", "GET POST FROM DATABASE: $localPosts")
                if (localPosts.isNotEmpty()) {
                    postsCache.addAll(localPosts.map { it.toPost() }) // Convert PostEntity to Post
                } else {
                    // Fetch from API
                    val apiPosts = postApiService.getPosts()
                    Log.d("FLOW", "GET POSTS FROM API: $apiPosts")
                    postsCache.addAll(apiPosts)

                    // Save posts and related data in the database
                    withContext(Dispatchers.IO) {
                        apiPosts.forEach { post ->
                            savePost(post) // Save post, tags, and images
                        }
                    }
                }
            }
            postsCache
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching posts: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun loadTags():List<Tag>{
        return tagDao.loadTags().map { tag -> tag.toTag() }
    }

    override suspend fun getPostById(id: Long): Post? {
        // Fetch a single post from cache or database
        return postsCache.find { it.id == id } ?: withContext(Dispatchers.IO) {
            postDao.getPostWithTagsAndImage(id)?.toPost() // Convert PostWithTags to Post
        }
    }

    // Save post, tags, and images into the database
    private suspend fun savePost(post: Post) {
        withContext(Dispatchers.IO) {
            try {
                // Save the PostEntity
                Log.d("FLOW", "USER in the post: ${post.patron}")
                val postEntity = post.patron?.let { post.toPostEntity(it.id) }
                if (postEntity == null) {
                    Log.d("FLOW", "USER NULL")
                    throw IllegalArgumentException("No user provided")
                }
                postDao.insertPost(postEntity)
                // Save the associated image if present
                post.image?.let { image ->
                    val imageEntity = image.toImageEntity(postId = post.id)
                    Log.d("FLOW", "IMAGE SAVING image: $image and imageEntity: $imageEntity")
                    postDao.insertImage(imageEntity)
                }

                // Save tags and create cross-references for the post
                post.tags?.let { tags ->
                    val tagEntities = tags.map { it.toTagEntity() }
                    tagDao.insertTags(tagEntities) // Save tags first
                    Log.d("FLOW", "TAG saving to the database $tagEntities")
                    val postWithTags = tags.map { tag -> PostWithTags(tagId = tag.id, postId = post.id) }
                    postDao.insertPostsWithTags(postWithTags) // Insert the many-to-many relationships
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "Error saving post: ${e.message}", e)
            }
        }
    }

    suspend fun createPost(postDTO: PostDTO, imageUrl: String){
        withContext(Dispatchers.IO){
            try {
                // fetch from uri
//                postDTO.file = getImageFromUrl(imageUrl)
                Log.d("FLOW", "POST DTO postDTO: $postDTO")
                val newFetchedPost = postApiService.createPost(postDTO)
                newFetchedPost?.let { savePost(newFetchedPost) }
            } catch (e: Exception){
                Log.e("FLOW", "Error creating post $e")
            }
        }
    }




    suspend fun updateDatabasePosts(): List<Post>{
        return  withContext(Dispatchers.IO){
            try {
                //first getting all posts, if failed save current posts
                val apiPosts = postApiService.getPosts()

                postsCache.clear()
                postDao.deleteAllPosts()

                apiPosts.forEach { post ->
                    savePost(post)
                }
                postsCache.addAll(apiPosts)
                apiPosts
            } catch (e : Exception){
                Log.d("FLOW", "UPDATE POSTS FAILED with API")
                postsCache
            }
        }
    }
}
