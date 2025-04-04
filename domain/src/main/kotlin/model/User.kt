package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("bio") val bio: String,
    @SerialName("avatar") val avatar: String?,
    @SerialName("followers_count") val followersCount: Int,
    @SerialName("following_count") val followingCount: Int,
)