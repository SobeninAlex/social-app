package model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.dto.User

@Serializable
data class SingUpRequest(
    @SerialName("user_name") val name: String,
    @SerialName("user_email") val email: String,
    @SerialName("user_password") val password: String,
) {

    companion object {
        fun SingUpRequest.toUser() = User(
            name = name,
            email = email,
            password = password
        )
    }
}
