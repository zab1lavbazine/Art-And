package art.example.api.service

import art.example.api.data.User
import art.example.api.reponses.LoginResponse
import art.example.api.reponses.UserCredentials
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApiService {

    @GET("/api/users")
    suspend fun getUsers():List<User>

    @GET("/api/users/{id}")
    suspend fun getUserById(): User?

    @POST("/api/login")
    suspend fun login(@Body credentials: UserCredentials): LoginResponse

    @GET("/api/users/{username}")
    suspend fun getUserByUsername(username: String): User?

}
