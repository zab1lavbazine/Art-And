package art.example.api.service

import art.example.api.data.DTO.PostDTO
import art.example.api.data.Post
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApiService {

    @GET("/api/recommendations/posts")
    suspend fun getPosts(
        @retrofit2.http.Query("page") pageNumber: Int,
        @retrofit2.http.Query("size") pageSize: Int,
    ): ResponseItem<Post>

    @GET("/api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Post?

    @POST("/api/posts")
    suspend fun createPost(@Body postDTO: PostDTO): Post?
}


data class ResponseItem<T> @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("content") val content : List<T>

)