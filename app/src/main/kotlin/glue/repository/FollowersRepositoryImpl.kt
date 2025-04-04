package glue.repository

import ErrorMessage
import Response
import io.ktor.http.*
import model.response.BaseResponse
import repository.FollowersRepository
import follows.FollowsDao
import user.UserDao

class FollowersRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao,
) : FollowersRepository {

    override suspend fun followUser(follower: String, following: String): Response<BaseResponse> {
        return if (followsDao.isAlreadyFollowing(follower, following)) {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = BaseResponse(
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
                    data = BaseResponse(
                        isSuccess = true,
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = BaseResponse(
                        isSuccess = false,
                        errorMessage = ErrorMessage.SOMETHING_WRONG
                    )
                )
            }
        }
    }

    override suspend fun unfollowUser(follower: String, following: String): Response<BaseResponse> {
        val success = followsDao.unfollowUser(follower, following)
        return if (success) {
            userDao.updateFollowsCount(follower, following, false)
            Response.Success(
                code = HttpStatusCode.OK,
                data = BaseResponse(isSuccess = true)
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = BaseResponse(
                    isSuccess = false,
                    errorMessage = ErrorMessage.SOMETHING_WRONG
                )
            )
        }
    }
}