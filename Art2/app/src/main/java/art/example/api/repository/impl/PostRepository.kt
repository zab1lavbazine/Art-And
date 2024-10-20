package art.example.api.repository.impl

import art.example.api.data.Post
import art.example.api.repository.IPostApiService
import art.example.api.service.PostApiService


// Repository for fetching posts data
class PostRepository(
    private val postApiService: PostApiService // Dependency injected PostApiService
): IPostApiService {

    // In-memory cache to store posts once loaded
    private val postsCache = mutableListOf<Post>()

    // Static list of posts used as mock data
    private val staticPosts = listOf(
        Post(id = 1, title = "Post 1", description = "This is the first post.", imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRujbJkg9ZMrOOWIE0YovMK9L6zRwOGKKTOmw&s"),
        Post(id = 2, title = "Post 2", description = "This is the second post.", imageUrl = "https://i.pinimg.com/236x/e6/aa/f8/e6aaf8816c655e914274937ea36dc103.jpg"),
        Post(id = 3, title = "Post 3", description = "This is the third post.", imageUrl = "https://img.freepik.com/free-photo/majestic-mountain-peak-tranquil-winter-landscape-generated-by-ai_188544-15662.jpg")
    )

    // Function to get the list of posts
    override suspend fun getPosts(): List<Post> {
        // Return static data instead of making API calls
        if (postsCache.isEmpty()) {
            // Add static posts to cache
            postsCache.addAll(staticPosts)
        }
        // Return the cached posts
        return postsCache
    }

    // Function to get a post by its ID
    override suspend fun getPostById(id: Long): Post? {
        // First, check if the post is in the cache
        val cachedPost = postsCache.find { it.id == id }

        // If found in cache, return it, otherwise, return static data
        return cachedPost ?: staticPosts.find { it.id == id }
    }
}
