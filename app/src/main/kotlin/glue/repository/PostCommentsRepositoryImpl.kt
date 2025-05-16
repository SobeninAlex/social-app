package glue.repository

import ErrorMessage
import Response
import glue.toPostComment
import io.ktor.http.*
import model.request.NewCommentRequest
import model.response.CommentResponse
import model.response.ListCommentResponse
import model.response.SimpleResponse
import post.PostDao
import post_comments.PostCommentsDao
import repository.PostCommentsRepository

class PostCommentsRepositoryImpl(
    private val postCommentsDao: PostCommentsDao,
    private val postDao: PostDao
) : PostCommentsRepository {

    override suspend fun addComment(request: NewCommentRequest): Response<CommentResponse> {
        val postCommentRow = postCommentsDao.addComment(
            postId = request.postId,
            userId = request.userId,
            content = request.content
        )

        return if (postCommentRow == null) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = CommentResponse(
                    isSuccess = false,
                    errorMessage = ErrorMessage.SOMETHING_WRONG
                )
            )
        } else {
            postDao.updateCommentsCount(postId = request.postId)
            Response.Success(
                code = HttpStatusCode.Created,
                data = CommentResponse(
                    isSuccess = true,
                    postComment = postCommentRow.toPostComment()
                )
            )
        }
    }

    override suspend fun deleteComment(
        commentId: String,
        postId: String,
        userId: String
    ): Response<SimpleResponse> {
        val commentRow = postCommentsDao.findComment(
            commentId = commentId,
            postId = postId
        )

        if (commentRow == null) {
            return Response.Error(
                code = HttpStatusCode.NotFound,
                data = SimpleResponse(
                    isSuccess = false,
                    errorMessage = "Comment not found"
                )
            )
        }

        val postOwnerId = postDao.getPost(postId = postId)?.userId

        if (userId != commentRow.userId && userId != postOwnerId) {
            return Response.Error(
                code = HttpStatusCode.Forbidden,
                data = SimpleResponse(
                    isSuccess = false,
                    errorMessage = "User $userId cannot delete comment $commentId"
                )
            )
        }

        val isSuccessDeleted = postCommentsDao.deleteComment(
            commentId = commentId,
            postId = postId
        )

        return if (isSuccessDeleted) {
            postDao.updateCommentsCount(postId = postId, decrement = true)
            Response.Success(
                code = HttpStatusCode.Created,
                data = SimpleResponse(isSuccess = true)
            )
        } else {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = SimpleResponse(
                    isSuccess = false,
                    errorMessage = ErrorMessage.SOMETHING_WRONG
                )
            )
        }
    }

    override suspend fun getPostComments(
        postId: String,
        page: Int,
        pageSize: Int
    ): Response<ListCommentResponse> {
        val comments = postCommentsDao.getComments(
            postId = postId,
            page = page,
            pageSize = pageSize
        ).map { it.toPostComment() }

        return Response.Success(
            code = HttpStatusCode.OK,
            data = ListCommentResponse(
                isSuccess = true,
                postComments = comments
            )
        )
    }
}