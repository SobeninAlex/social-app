package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("bio") val bio: String,
    @SerialName("avatar") val avatar: String?,
    @SerialName("token") val token: String,
    @SerialName("followers_count") val followersCount: Int,
    @SerialName("following_count") val followingCount: Int,
)