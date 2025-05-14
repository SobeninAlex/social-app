package repository

import Response
import model.request.NewCommentRequest
import model.response.CommentResponse
import model.response.ListCommentResponse
import model.response.SimpleResponse

interface PostCommentsRepository {

    suspend fun addComment(request: NewCommentRequest): Response<CommentResponse>

    suspend fun deleteComment(commentId: String, postId: String, userId: String): Response<SimpleResponse>

    suspend fun getPostComments(postId: String, pageNumber: Int, pageSize: Int): Response<ListCommentResponse>
}