package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    @SerialName("post_id") val postId: String,
    @SerialName("caption") val caption: String,
    @SerialName("image_urls") val imageUrls: List<String>,
    @SerialName("likes_count") val likesCount: Int,
    @SerialName("comments_count") val commentsCount: Int,
    @SerialName("user_id") val userId: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("user_name") val userName: String,
    @SerialName("user_avatar") val userAvatar: String?,
    @SerialName("is_liked") val isLiked: Boolean?,
    @SerialName("is_own_post") val isOwnPost: Boolean?,
)
