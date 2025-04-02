package glue.repository

import ErrorMessage
import Response
import io.ktor.http.*
import model.response.BaseResponse
import repository.FollowersRepository
import table.FollowsTable
import table.UserTable

class FollowersRepositoryImpl(
    private val userTable: UserTable,
    private val followsTable: FollowsTable,
) : FollowersRepository {

    override suspend fun followUser(follower: String, following: String): Response<BaseResponse> {
        return if (FollowsTable.isAlreadyFollowing(follower, following)) {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = BaseResponse(
                    isSuccess = false,
                    message = "You are already following this user!",
                )
            )
        } else {
            val success = followsTable.followUser(follower, following)
            if (success) {
                userTable.updateFollowsCount(follower, following, true)
                Response.Success(
                    code = HttpStatusCode.OK,
                    data = BaseResponse(
                        isSuccess = true
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = BaseResponse(
                        isSuccess = false,
                        message = ErrorMessage.SOMETHING_WRONG
                    )
                )
            }
        }
    }

    override suspend fun unfollowUser(follower: String, following: String): Response<BaseResponse> {
        val success = followsTable.unfollowUser(follower, following)
        return if (success) {
            userTable.updateFollowsCount(follower, following, false)
            Response.Success(
                code = HttpStatusCode.OK,
                data = BaseResponse(isSuccess = true)
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = BaseResponse(
                    isSuccess = false,
                    message = ErrorMessage.SOMETHING_WRONG
                )
            )
        }
    }
}