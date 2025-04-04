package post

data class PostRow(
    val postId: String = "",
    val caption: String,
    val imageUrl: String,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val userId: String,
    val createdAt: String,
    val userName: String,
    val userAvatar: String?,
)