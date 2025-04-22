package model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostLikeRequest(
    @SerialName("post_id") val postId: String,
    @SerialName("user_id") val userId: String
)