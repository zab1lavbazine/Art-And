package art.example.api.repository

import art.example.api.data.Post

interface IPostApiService {
    suspend fun getPosts(): List<Post>
    suspend fun getPostById(id: Long): Post?
}