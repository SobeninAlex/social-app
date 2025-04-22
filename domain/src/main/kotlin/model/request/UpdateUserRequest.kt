package model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("name") val name: String,
    @SerialName("bio") val bio: String,
    @SerialName("avatar") val avatar: String? = null
)
