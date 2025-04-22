package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostComment(
    @SerialName("comment_id") val commentId: String,
    @SerialName("post_id") val postId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("content") val content: String,
    @SerialName("user_name") val userName: String,
    @SerialName("avatar") val avatar: String?,
    @SerialName("created_at") val createdAt: String,
)
