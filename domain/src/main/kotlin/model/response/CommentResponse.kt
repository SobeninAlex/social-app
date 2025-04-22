package model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.PostComment

@Serializable
data class CommentResponse(
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("post_comment") val postComment: PostComment? = null,
    @SerialName("error_message") val errorMessage: String? = null,
)
