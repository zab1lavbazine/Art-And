package art.example.api.service

import art.example.api.data.DTO.PostDTO
import art.example.api.data.Post
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApiService {

    @GET("/api/posts")
    suspend fun getPosts(): List<Post>

    @GET("/api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Post?

    @POST("/api/posts")
    suspend fun createPost(@Body postDTO: PostDTO): Post?
}