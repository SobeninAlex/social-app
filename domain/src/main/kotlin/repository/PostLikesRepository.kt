package repository

import Response
import model.request.PostLikeRequest
import model.response.SimpleResponse

interface PostLikesRepository {

    suspend fun addLike(request: PostLikeRequest): Response<SimpleResponse>

    suspend fun removeLike(request: PostLikeRequest): Response<SimpleResponse>
}