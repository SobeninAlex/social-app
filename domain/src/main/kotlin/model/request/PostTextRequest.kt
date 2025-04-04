package model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostTextRequest(
    @SerialName("caption") val caption: String,
    @SerialName("user_id") val userId: String,
)
