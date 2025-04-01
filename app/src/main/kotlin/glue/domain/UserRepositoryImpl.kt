package glue.domain

import ErrorMessage
import Response
import io.ktor.http.*
import model.AuthData
import model.AuthResponse
import model.SingInParams
import model.SingUpParams
import plugins.generateToken
import plugins.hashPassword
import repository.UserRepository
import user.UserDao

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun signUp(params: SingUpParams): Response<AuthResponse> {
        return if (userAlreadyExists(params.email)) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = AuthResponse(
                    errorMessage = ErrorMessage.USER_EXIST
                )
            )
        } else {
            userDao.insert(params)?.let { user ->
                Response.Success(
                    code = HttpStatusCode.Created,
                    data = AuthResponse(
                        authData = AuthData(
                            id = user.id,
                            name = user.name,
                            bio = user.bio,
                            avatar = user.avatar,
                            token = generateToken(params.email),
                            followersCount = 0,
                            followingCount = 0,
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

    override suspend fun signIn(params: SingInParams): Response<AuthResponse> {
        val user = userDao.findUserByEmail(params.email)
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
                            followersCount = 0,
                            followingCount = 0,
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
        return userDao.findUserByEmail(email) != null
    }
}