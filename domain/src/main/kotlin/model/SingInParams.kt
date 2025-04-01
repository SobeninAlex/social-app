package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingInParams(
    @SerialName("user_email") val email: String,
    @SerialName("user_password") val password: String,
)