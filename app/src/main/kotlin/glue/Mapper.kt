package glue

import model.Post
import model.User
import model.request.SingUpRequest
import post.PostRow
import user.UserRow

fun SingUpRequest.toUserRow() = UserRow(
    userName = name,
    userEmail = email,
    userPassword = password,
)

fun PostRow.toPost(isLiked: Boolean?, isOwnPost: Boolean?) = Post(
    postId = postId,
    caption = caption,
    imageUrl = imageUrl,
    likesCount = likesCount,
    commentsCount = commentsCount,
    userId = userId,
    createdAt = createdAt,
    userName = userName,
    userAvatar = userAvatar,
    isLiked = isLiked,
    isOwnPost = isOwnPost,
)

fun UserRow.toUser() = User(
    id = userId,
    name = userName,
    email = userEmail,
    bio = userBio,
    avatar = userAvatar,
    followersCount = followersCount,
    followingCount = followingCount,
)