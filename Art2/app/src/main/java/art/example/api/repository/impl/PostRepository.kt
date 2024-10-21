package art.example.api.repository.impl

import android.util.Log
import art.example.api.data.*
import art.example.api.repository.IPostApiService
import art.example.api.service.PostApiService
import art.example.database.*
import art.example.database.PostDao.PostDao
import art.example.database.TagDao.TagDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(
    private val postApiService: PostApiService, // API Service
    private val postDao: PostDao, // Post DAO
    private val tagDao: TagDao // Tag DAO
) : IPostApiService {

    // In-memory cache
    private val postsCache = mutableListOf<Post>()

    // Fetch all posts (from cache, database, or API)
    override suspend fun getPosts(): List<Post> {
        try {
            if (postsCache.isEmpty()) {
                val localPosts = withContext(Dispatchers.IO) { postDao.getAllPostsWithDetails() }
                if (localPosts.isNotEmpty()) {
                    postsCache.addAll(localPosts.map { it.toPost() })
                } else {
                    val apiPosts = postApiService.getPosts()
                    postsCache.addAll(apiPosts)

                    // Save posts and related data in the database
                    withContext(Dispatchers.IO) {
                        apiPosts.forEach { post ->
                            savePost(post)
                        }
                    }
                }
            }
            return postsCache
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching posts: ${e.message}", e)
            return emptyList()
        }
    }

    override suspend fun getPostById(id: Long): Post? {
        // Fetch a single post from cache or database
        return postsCache.find { it.id == id } ?: withContext(Dispatchers.IO) {
            postDao.getPostWithTags(id)?.toPost()
        }
    }

    // Save post, tags, and image into the database
    suspend fun savePost(post: Post) {
        withContext(Dispatchers.IO) {
            try {
                // Save the PostEntity
                val postEntity = post.toPostEntity(userId = post.patron?.id ?: 0L)
                postDao.insertPost(postEntity)

                // Save the associated image if present
                post.image?.let { image ->
                    val imageEntity = image.toImageEntity(postId = post.id)
                    postDao.insertImages(listOf(imageEntity))
                }

                // Save tags and create cross-references for the post
                post.tags?.let { tags ->
                    val tagEntities = tags.map { it.toTagEntity() }
                    tagDao.insertTagsForPost(post.id, tagEntities)
                }

            } catch (e: Exception) {
                Log.e("PostRepository", "Error saving post: ${e.message}", e)
            }
        }
    }
}
