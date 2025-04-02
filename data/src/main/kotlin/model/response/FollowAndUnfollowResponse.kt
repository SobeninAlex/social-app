package model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowAndUnfollowResponse(
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("message") val message: String? = null
)
