package art.example.api.service

import art.example.api.data.ResetPasswordRequest
import art.example.api.data.SelectedUser
import art.example.api.data.User
import art.example.api.reponses.LoginResponse
import art.example.api.reponses.RegisterUserDTO
import art.example.api.reponses.UserCredentials
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @GET("/api/users/account")
    suspend fun getUserAccount(): User?

    @POST("/api/auth/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body credentials: UserCredentials): LoginResponse

    @POST("/api/auth/signup")
    suspend fun register(@Body credentials: RegisterUserDTO)

    @PUT("/api/users")
    suspend fun updateUserInfo(@Body updatedUser: User): Map<String, String>


    @GET("/api/users/{userId}")
    suspend fun getSelectedUserById(@Path("userId") userId: Long): SelectedUser?


    @POST("/api/auth/reset-password-request")
    suspend fun resetPassword(@Query("email") email: String): String?

    @PUT("/api/auth/reset-password")
    @Headers("Content-Type: application/json")
    suspend fun sendNewPassword(@Body request: ResetPasswordRequest)

}
