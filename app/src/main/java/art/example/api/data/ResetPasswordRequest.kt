package art.example.api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ResetPasswordRequest(
    @SerialName("token")
    val token: String,
    @SerialName("password")
    val password: String,
    @SerialName("confirmPassword")
    val confirmPassword: String
)
