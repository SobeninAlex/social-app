package glue.repository

import Response
import io.ktor.http.HttpStatusCode
import model.request.PostLikeRequest
import model.response.SimpleResponse
import post.PostDao
import post_like.PostLikeDao
import repository.PostLikesRepository

class PostLikesRepositoryImpl(
    private val likeDao: PostLikeDao,
    private val postDao: PostDao,
) : PostLikesRepository {

    override suspend fun addLike(request: PostLikeRequest): Response<SimpleResponse> {
        val likeExist = likeDao.isPostLikedByUser(
            postId = request.postId,
            userId = request.userId,
        )
        if (likeExist) {
            return Response.Error(
                code = HttpStatusCode.Conflict,
                data = SimpleResponse(
                    isSuccess = false,
                    errorMessage = "post already liked"
                )
            )
        }

        val postExist = postDao.getPost(postId = request.postId)
        if (postExist == null) {
            return Response.Error(
                code = HttpStatusCode.Conflict,
                data = SimpleResponse(
                    isSuccess = false,
                    errorMessage = "post not exist"
                )
            )
        }

        val isSuccess = likeDao.addLike(
            postId = request.postId,
            userId = request.userId,
        )

        return if (isSuccess) {
            postDao.updateLikesCount(postId = request.postId)
            Response.Success(
                code = HttpStatusCode.OK,
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

    override suspend fun removeLike(request: PostLikeRequest): Response<SimpleResponse> {
        val likeExist = likeDao.isPostLikedByUser(
            postId = request.postId,
            userId = request.userId,
        )

        if (!likeExist) {
            return Response.Error(
                code = HttpStatusCode.Conflict,
                data = SimpleResponse(
                    isSuccess = false,
                    errorMessage = "post not liked"
                )
            )
        }

        val isSuccess = likeDao.removeLike(
            postId = request.postId,
            userId = request.userId,
        )

        return if (isSuccess) {
            postDao.updateLikesCount(postId = request.postId, decrement = true)
            Response.Success(
                code = HttpStatusCode.OK,
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
}