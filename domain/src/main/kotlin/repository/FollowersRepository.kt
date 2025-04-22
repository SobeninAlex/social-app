package repository

import Response
import model.response.FollowsResponse
import model.response.SimpleResponse

interface FollowersRepository {

    suspend fun followUser(follower: String, following: String): Response<SimpleResponse>

    suspend fun unfollowUser(follower: String, following: String): Response<SimpleResponse>

    suspend fun getFollowers(userId: String, pageNumber: Int, pageSize: Int): Response<FollowsResponse>

    suspend fun getFollowing(userId: String, pageNumber: Int, pageSize: Int): Response<FollowsResponse>

    suspend fun getFollowingSuggestions(userId: String): Response<FollowsResponse>
}