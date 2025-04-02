package repository

import Response
import model.response.BaseResponse

interface FollowersRepository {

    suspend fun followUser(follower: String, following: String): Response<BaseResponse>

    suspend fun unfollowUser(follower: String, following: String): Response<BaseResponse>
}