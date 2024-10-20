package art.example.api.service

import art.example.api.data.Post
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApiService {

    @GET("/api/posts")
    suspend fun getPosts(): List<Post>

    @GET("/api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Post?
}