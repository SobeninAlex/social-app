package model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.Post

@Serializable
data class PostsResponse(
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("posts") val posts: List<Post> = emptyList(),
    @SerialName("error_message") val errorMessage: String? = null,
)
