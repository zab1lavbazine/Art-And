package com.example.arthub.api.repository

import com.example.arthub.api.data.Post
import com.example.arthub.api.service.PostApiService

// Repository for fetching posts data
class PostRepository(
    private val postApiService: PostApiService // Dependency injected PostApiService
) {

    // In-memory cache to store posts once loaded
    private val postsCache = mutableListOf<Post>()

    // Static list of posts used as mock data
    private val staticPosts = listOf(
        Post(id = 1, title = "Post 1", description = "This is the first post."),
        Post(id = 2, title = "Post 2", description = "This is the second post."),
        Post(id = 3, title = "Post 3", description = "This is the third post.")
    )

    // Function to get the list of posts
    suspend fun getPosts(): List<Post> {
        // Return static data instead of making API calls
        if (postsCache.isEmpty()) {
            // Add static posts to cache
            postsCache.addAll(staticPosts)
        }
        // Return the cached posts
        return postsCache
    }

    // Function to get a post by its ID
    suspend fun getPostById(id: Long): Post? {
        // First, check if the post is in the cache
        val cachedPost = postsCache.find { it.id == id }

        // If found in cache, return it, otherwise, return static data
        return cachedPost ?: staticPosts.find { it.id == id }
    }
}
