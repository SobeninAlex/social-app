package repository

import Response
import model.response.FollowAndUnfollowResponse

interface FollowersRepository {

    suspend fun followUser(follower: String, following: String): Response<FollowAndUnfollowResponse>

    suspend fun unfollowUser(follower: String, following: String): Response<FollowAndUnfollowResponse>
}