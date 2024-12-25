package art.example.api.service

import art.example.api.data.DTO.PostDTO
import art.example.api.data.Post
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface PostApiService {

    @GET("/api/recommendations/posts")
    suspend fun getPosts(
        @retrofit2.http.Query("page") pageNumber: Int,
        @retrofit2.http.Query("size") pageSize: Int,
    ): ResponseItem<Post>

    @POST("/api/posts/search")
    suspend fun searchPosts(
        @retrofit2.http.Query("keyword") query : String,
        @retrofit2.http.Query("page") pageNumber: Int,
        @retrofit2.http.Query("size") pageSize: Int,
    ) : ResponseItem<Post>

    @GET("/api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Post?

    @Multipart
    @POST("/api/posts")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("tagsId") tagsId: RequestBody,
        @Part file: MultipartBody.Part? // Accept the file as a multipart part
    ): Post?

    @GET("/api/posts/user/{userId}")
    suspend fun getPostsByUserId(@Path("userId") userId: Long): List<Post>


    @DELETE("/api/posts/{postId}")
    suspend fun deletePostById(@Path("postId") postId: Long)


    @Multipart
    @PUT("/api/posts/{postId}")
    suspend fun updatePostById(@Path("postId") postId: Long,
                               @Part("title") title: RequestBody,
                               @Part("description") description: RequestBody,
                               @Part("tagsId") tagsId: RequestBody,
                               @Part file: MultipartBody.Part?
    ): Post?


}


data class ResponseItem<T> @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("content") val content : List<T>

)