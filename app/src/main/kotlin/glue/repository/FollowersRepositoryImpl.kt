package glue.repository

import Constants
import ErrorMessage
import Response
import io.ktor.http.*
import model.response.SimpleResponse
import repository.FollowersRepository
import follows.FollowsDao
import model.FollowUserData
import model.response.FollowsResponse
import user.UserDao
import user.UserRow

class FollowersRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao,
) : FollowersRepository {

    override suspend fun getFollowers(
        userId: String,
        page: Int,
        pageSize: Int
    ): Response<FollowsResponse> {
        val followerIDs = followsDao.getFollowers(
            userId = userId,
            page = page,
            pageSize = pageSize
        )

        val followersRows = userDao.getUsers(ids = followerIDs)

        val followers = followersRows.map { row ->
            val isFollowing = followsDao.isAlreadyFollowing(follower = userId, following = row.userId)
            row.toFollowUserData(isFollowing)
        }

        return Response.Success(
            code = HttpStatusCode.OK,
            data = FollowsResponse(
                isSuccess = true,
                follows = followers
            )
        )
    }

    override suspend fun getFollowing(
        userId: String,
        page: Int,
        pageSize: Int
    ): Response<FollowsResponse> {
        val followingIDs = followsDao.getFollowing(
            userId = userId,
            page = page,
            pageSize = pageSize
        )

        val followingRows = userDao.getUsers(ids = followingIDs)

        val following = followingRows.map { row ->
            row.toFollowUserData(isFollowing = true)
        }
        return Response.Success(
            code = HttpStatusCode.OK,
            data = FollowsResponse(
                isSuccess = true,
                follows = following
            )
        )
    }

    override suspend fun followUser(follower: String, following: String): Response<SimpleResponse> {
        return if (followsDao.isAlreadyFollowing(follower, following)) {
            Response.Error(
                code = HttpStatusCode.BadRequest,
                data = SimpleResponse(
                    isSuccess = false,
                    errorMessage = "You are already following this user!",
                )
            )
        } else {
            val success = followsDao.followUser(follower, following)
            if (success) {
                userDao.updateFollowsCount(follower, following, true)
                Response.Success(
                    code = HttpStatusCode.OK,
                    data = SimpleResponse(
                        isSuccess = true,
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = SimpleResponse(
                        isSuccess = false,
                        errorMessage = ErrorMessage.SOMETHING_WRONG
                    )
                )
            }
        }
    }

    override suspend fun unfollowUser(follower: String, following: String): Response<SimpleResponse> {
        if (!followsDao.isAlreadyFollowing(follower, following)) {
            return Response.Error(
                code = HttpStatusCode.BadRequest,
                data = SimpleResponse(
                    isSuccess = false,
                    errorMessage = "You are not following this user!",
                )
            )
        }
        val success = followsDao.unfollowUser(follower, following)
        return if (success) {
            userDao.updateFollowsCount(follower, following, false)
            Response.Success(
                code = HttpStatusCode.OK,
                data = SimpleResponse(isSuccess = true)
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = SimpleResponse(
                    isSuccess = false,
                    errorMessage = ErrorMessage.SOMETHING_WRONG
                )
            )
        }
    }

    override suspend fun getFollowingSuggestions(userId: String): Response<FollowsResponse> {
        val hasFollowing = followsDao.getFollowing(
            userId = userId,
            page = Constants.DEFAULT_PAGE,
            pageSize = Constants.DEFAULT_PAGE_SIZE
        )

        val suggestedFollowing = userDao
            .getPopularUsers(limit = Constants.SUGGESTED_FOLLOWING_LIMIT)
            .filterNot { it.userId == userId }
            .filterNot { hasFollowing.contains(it.userId) }
            .map { it.toFollowUserData(isFollowing = hasFollowing.contains(it.userId)) }

        return Response.Success(
            code = HttpStatusCode.OK,
            data = FollowsResponse(
                isSuccess = true,
                follows = suggestedFollowing
            )
        )
    }

    private fun UserRow.toFollowUserData(isFollowing: Boolean): FollowUserData {
        return FollowUserData(
            id = this.userId,
            name = this.userName,
            bio = this.userBio,
            avatar = this.userAvatar,
            isFollowing = isFollowing
        )
    }
}