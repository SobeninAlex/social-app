package user

data class UserRow(
    val userId: String = "",
    val userName: String,
    val userEmail: String,
    val userBio: String = "",
    val userPassword: String,
    val userAvatar: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
)