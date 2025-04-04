package glue.repository

import ErrorMessage
import Response
import io.ktor.http.*
import model.request.PostTextRequest
import model.response.PostResponse
import model.response.PostsResponse
import repository.PostRepository
import follows.FollowsDao
import glue.toPost
import post.PostDao
import post_like.PostLikeDao

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val followsDao: FollowsDao,
    private val postLikeDao: PostLikeDao
) : PostRepository {

    override suspend fun createPost(imageUrl: String, postTextRequest: PostTextRequest): Response<PostResponse> {
        val postIsCreated = postDao.createPost(
            caption = postTextRequest.caption,
            imageUrl = imageUrl,
            userId = postTextRequest.userId
        )
        return if (postIsCreated) {
            Response.Success(
                code = HttpStatusCode.Created,
                data = PostResponse(isSuccess = true)
            )
        } else {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = PostResponse(
                    isSuccess = false,
                    errorMessage = ErrorMessage.SOMETHING_WRONG
                )
            )
        }
    }

    override suspend fun getFeedPosts(userId: String, pageNumber: Int, pageSize: Int): Response<PostsResponse> {
        val followingUsers = followsDao.getAllFollowing(userId = userId).toMutableList()
        followingUsers.add(userId)

        val posts = postDao.getFeedPosts(
            follows = followingUsers,
            pageNumber = pageNumber,
            pageSize = pageSize
        ).map { post ->
            post.toPost(
                isLiked = postLikeDao.isPostLikedByUser(postId = post.postId, userId = userId),
                isOwnPost = post.userId == userId
            )
        }

        return Response.Success(
            code = HttpStatusCode.OK,
            data = PostsResponse(
                isSuccess = true,
                posts = posts
            )
        )
    }

    override suspend fun getPostsByUser(
        postsOwnerId: String,
        currentUserId: String,
        pageNumber: Int,
        pageSize: Int
    ): Response<PostsResponse> {
        val posts = postDao.getPostsByUserId(
            userId = postsOwnerId,
            pageNumber = pageNumber,
            pageSize = pageSize
        ).map { post ->
            post.toPost(
                isLiked = postLikeDao.isPostLikedByUser(postId = post.postId, userId = currentUserId),
                isOwnPost = post.userId == currentUserId
            )
        }

        return Response.Success(
            code = HttpStatusCode.OK,
            data = PostsResponse(
                isSuccess = true,
                posts = posts
            )
        )
    }

    override suspend fun getPost(postId: String, userId: String): Response<PostResponse> {
        val postRow = postDao.getPost(postId = postId, userId = userId)

        return if (postRow != null) {
            val isPostLiked = postLikeDao.isPostLikedByUser(postId = postId, userId = userId)
            val isOwnPost = postRow.postId == userId
            Response.Success(
                code = HttpStatusCode.OK,
                data = PostResponse(
                    isSuccess = true,
                    post = postRow.toPost(isLiked = isPostLiked, isOwnPost = isOwnPost)
                )
            )
        } else {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = PostResponse(
                    isSuccess = false,
                    errorMessage = ErrorMessage.POST_NOT_FOUND
                )
            )
        }
    }

    override suspend fun getPost(postId: String): Response<PostResponse> {
        val postRow = postDao.getPost(postId = postId)

        return if (postRow != null) {
            Response.Success(
                code = HttpStatusCode.OK,
                data = PostResponse(
                    isSuccess = true,
                    post = postRow.toPost(isLiked = null, isOwnPost = null)
                )
            )
        } else {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = PostResponse(
                    isSuccess = false,
                    errorMessage = ErrorMessage.POST_NOT_FOUND
                )
            )
        }
    }

    override suspend fun deletePost(postId: String): Response<PostResponse> {
        val postIsDeleted = postDao.deletePost(postId = postId)
        return if (postIsDeleted) {
            Response.Success(
                code = HttpStatusCode.Created,
                data = PostResponse(isSuccess = true)
            )
        } else {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = PostResponse(
                    isSuccess = false,
                    errorMessage = ErrorMessage.SOMETHING_WRONG
                )
            )
        }
    }
}