package glue.repository

import ErrorMessage
import Response
import hashPassword
import io.ktor.http.*
import model.dto.AuthData
import model.request.SingInRequest
import model.request.SingUpRequest
import model.request.SingUpRequest.Companion.toUser
import model.response.AuthResponse
import plugins.generateToken
import repository.AuthRepository
import table.UserTable

class AuthRepositoryImpl(
    private val userTable: UserTable,
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
            userTable.insert(params.toUser())?.let { user ->
                Response.Success(
                    code = HttpStatusCode.Created,
                    data = AuthResponse(
                        authData = AuthData(
                            id = user.id,
                            name = user.name,
                            bio = user.bio,
                            avatar = user.avatar,
                            token = generateToken(params.email),
                            followersCount = user.followersCount,
                            followingCount = user.followingCount,
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
        val user = userTable.getUserByEmail(params.email)
        return if (user == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = AuthResponse(
                    errorMessage = ErrorMessage.USER_NOT_FOUND
                )
            )
        } else {
            val hashedPassword = hashPassword(params.password)
            if (user.password == hashedPassword) {
                Response.Success(
                    code = HttpStatusCode.OK,
                    data = AuthResponse(
                        authData = AuthData(
                            id = user.id,
                            name = user.name,
                            bio = user.bio,
                            avatar = user.avatar,
                            token = generateToken(params.email),
                            followersCount = user.followersCount,
                            followingCount = user.followingCount,
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
        return userTable.getUserByEmail(email) != null
    }
}