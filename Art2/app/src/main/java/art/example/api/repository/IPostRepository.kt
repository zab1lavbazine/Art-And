package art.example.api.repository

import art.example.api.data.Post

interface IPostRepository {
    suspend fun getPostById(id: Long): Post?
}