package model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.User

@Serializable
data class InfoUserResponse(
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("user") val user: User? = null,
    @SerialName("error_message") val errorMessage: String? = null,
)
