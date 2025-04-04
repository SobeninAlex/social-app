package glue.repository

import Response
import glue.toUser
import io.ktor.http.*
import model.response.InfoUserResponse
import repository.UserRepository
import user.UserDao

class UserRepositoryImpl(
    private val userDao: UserDao,
) : UserRepository {

    override suspend fun getUserByEmail(email: String): Response<InfoUserResponse> {
        val userRow = userDao.getUserByEmail(email)
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