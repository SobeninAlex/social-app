package glue.repository

import ErrorMessage
import Response
import glue.toUserRow
import hashPassword
import io.ktor.http.*
import model.AuthData
import model.request.SingInRequest
import model.request.SingUpRequest
import model.response.AuthResponse
import plugins.generateToken
import repository.AuthRepository
import user.UserDao

class AuthRepositoryImpl(
    private val userDao: UserDao,
): AuthRepository {

    override suspend fun signUp(params: SingUpRequest): Response<AuthResponse> {
        return if (userAlreadyExists(params.email)) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = AuthResponse(
                    errorMessage = ErrorMessage.USER_EXIST
                )
            )
        } else {
            userDao.insert(params.toUserRow())?.let { userRow ->
                Response.Success(
                    code = HttpStatusCode.Created,
                    data = AuthResponse(
                        authData = AuthData(
                            id = userRow.userId,
                            name = userRow.userName,
                            bio = userRow.userBio,
                            avatar = userRow.userAvatar,
                            token = generateToken(params.email),
                            followersCount = userRow.followersCount,
                            followingCount = userRow.followingCount,
                        )
                    )
                )
            } ?: Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = AuthResponse(
                    errorMessage = ErrorMessage.SOMETHING_WRONG
                )
            )
        }
    }

    override suspend fun signIn(params: SingInRequest): Response<AuthResponse> {
        val userRow = userDao.findByEmail(params.email)
        return if (userRow == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = AuthResponse(
                    errorMessage = ErrorMessage.USER_NOT_FOUND
                )
            )
        } else {
            val hashedPassword = hashPassword(params.password)
            if (userRow.userPassword == hashedPassword) {
                Response.Success(
                    code = HttpStatusCode.OK,
                    data = AuthResponse(
                        authData = AuthData(
                            id = userRow.userId,
                            name = userRow.userName,
                            bio = userRow.userBio,
                            avatar = userRow.userAvatar,
                            token = generateToken(params.email),
                            followersCount = userRow.followersCount,
                            followingCount = userRow.followingCount,
                        )
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = AuthResponse(
                        errorMessage = ErrorMessage.PASSWORD_INVALID
                    )
                )
            }
        }
    }

    private suspend fun userAlreadyExists(email: String): Boolean {
        return userDao.findByEmail(email) != null
    }
}