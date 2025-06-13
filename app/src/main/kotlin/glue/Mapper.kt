package glue

import model.Post
import model.PostComment
import model.User
import model.request.SingUpRequest
import post.PostRow
import post_comments.PostCommentsRow
import user.UserRow

fun SingUpRequest.toUserRow() = UserRow(
    userName = name,
    userEmail = email,
    userPassword = password,
)

fun PostRow.toPost(isLiked: Boolean?, isOwnPost: Boolean?) = Post(
    postId = postId,
    caption = caption,
    imageUrls = imageUrls,
    likesCount = likesCount,
    commentsCount = commentsCount,
    userId = userId,
    createdAt = createdAt,
    userName = userName,
    userAvatar = userAvatar,
    isLiked = isLiked,
    isOwnPost = isOwnPost,
)

fun UserRow.toUser(isFollowing: Boolean? = null, isOwnProfile: Boolean? = null) = User(
    id = userId,
    name = userName,
    email = userEmail,
    bio = userBio,
    avatar = userAvatar,
    followersCount = followersCount,
    followingCount = followingCount,
    isFollowing = isFollowing,
    isOwnProfile = isOwnProfile,
)

fun PostCommentsRow.toPostComment() = PostComment(
    commentId = commentId,
    postId = postId,
    userId = userId,
    content = content,
    userName = userName,
    avatar = avatar,
    createdAt = createdAt,
)