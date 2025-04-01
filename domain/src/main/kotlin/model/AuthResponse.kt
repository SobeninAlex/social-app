package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("auth_data") val authData: AuthData? = null,
    @SerialName("error_message") val errorMessage: String? = null
)