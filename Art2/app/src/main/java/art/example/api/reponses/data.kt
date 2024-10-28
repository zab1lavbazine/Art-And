package art.example.api.reponses

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    // expiration date in milliseconds
    val expiresIn: Long
)


@Serializable
data class RegisterUserDTO(
    val email: String,
    val username: String,
    var password: String
)