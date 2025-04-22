package model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowRequest(
    @SerialName("follower") val follower: String,
    @SerialName("following") val following: String,
)