package post_comments

data class PostCommentsRow(
    val commentId: String = "",
    val postId: String,
    val userId: String,
    val content: String,
    val userName: String,
    val avatar: String? = null,
    val createdAt: String = "",
)
