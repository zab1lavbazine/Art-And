package art.example.api.service

import art.example.api.data.Tag
import retrofit2.http.GET


interface TagApiService {

    @GET("/api/tags")
    suspend fun getTags(): List<Tag>
}