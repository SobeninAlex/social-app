package model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.Post

@Serializable
data class PostResponse(
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("post") val post: Post? = null,
    @SerialName("error_message") val errorMessage: String? = null,
)
