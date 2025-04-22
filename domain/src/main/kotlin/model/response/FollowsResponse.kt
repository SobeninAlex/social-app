package model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.FollowUserData

@Serializable
data class FollowsResponse(
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("follows") val follows: List<FollowUserData> = emptyList(),
    @SerialName("error_message") val errorMessage: String? = null
)
