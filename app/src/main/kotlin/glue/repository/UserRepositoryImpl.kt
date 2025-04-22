package glue.repository

import Response
import follows.FollowsDao
import glue.toUser
import io.ktor.http.*
import model.request.UpdateUserRequest
import model.response.InfoUserResponse
import repository.UserRepository
import user.UserDao

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao,
) : UserRepository {

    override suspend fun getUserById(
        id: String,
        currentUserId: String
    ): Response<InfoUserResponse> {
        val userRow = userDao.findById(id)

        return if (userRow == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = InfoUserResponse(
                    isSuccess = false,
                    errorMessage = "User with ID '$id' not exist"
                )
            )
        } else {
            val isFollowing = followsDao.isAlreadyFollowing(follower = currentUserId, following = id)
            val isOwnProfile = id == currentUserId
            Response.Success(
                code = HttpStatusCode.OK,
                data = InfoUserResponse(
                    isSuccess = true,
                    user = userRow.toUser(isFollowing = isFollowing, isOwnProfile = isOwnProfile)
                )
            )
        }
    }

    override suspend fun updateUser(updateUserRequest: UpdateUserRequest): Response<InfoUserResponse> {
        val userExist = userDao.findById(id = updateUserRequest.userId) != null

        if (userExist) {
            val userUpdated = userDao.update(
                id = updateUserRequest.userId,
                name = updateUserRequest.name,
                bio = updateUserRequest.bio,
                avatar = updateUserRequest.avatar
            )

            return if (userUpdated) {
                Response.Success(
                    code = HttpStatusCode.OK,
                    data = InfoUserResponse(
                        isSuccess = true,
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = InfoUserResponse(
                        isSuccess = false,
                        errorMessage = "could not update User with ID: ${updateUserRequest.userId}"
                    )
                )
            }
        } else {
            return Response.Error(
                code = HttpStatusCode.NotFound,
                data = InfoUserResponse(
                    isSuccess = false,
                    errorMessage = "User with ID ${updateUserRequest.userId} not exists"
                )
            )
        }
    }

    override suspend fun getUserByEmail(email: String): Response<InfoUserResponse> {
        val userRow = userDao.findByEmail(email)
        return if (userRow == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = InfoUserResponse(
                    isSuccess = false,
                    errorMessage = "User with email '$email' not exist"
                )
            )
        } else {
            Response.Success(
                code = HttpStatusCode.OK,
                data = InfoUserResponse(
                    isSuccess = true,
                    user = userRow.toUser()
                )
            )
        }
    }
}