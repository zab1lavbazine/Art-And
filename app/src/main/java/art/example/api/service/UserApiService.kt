package art.example.api.service

import art.example.api.data.User
import art.example.api.reponses.LoginResponse
import art.example.api.reponses.RegisterUserDTO
import art.example.api.reponses.UserCredentials
import art.example.screen.Screen
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserApiService {

    @GET("/api/users")
    suspend fun getUsers():List<User>

    @GET("/api/users/account")
    suspend fun getUserAccount(): User?

    @POST("/api/auth/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body credentials: UserCredentials): LoginResponse

    @POST("/api/auth/signup")
    suspend fun register(@Body credentials: RegisterUserDTO)

    @GET("/api/users/account")
    suspend fun getUserByUsername(): User?

}
