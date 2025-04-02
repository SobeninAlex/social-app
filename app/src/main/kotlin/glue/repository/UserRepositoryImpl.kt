package glue.repository

import Response
import io.ktor.http.*
import model.dto.User.Companion.toUserInfoResponse
import model.response.BaseResponse
import model.response.UserInfoResponse
import repository.UserRepository
import table.UserTable

class UserRepositoryImpl(
    private val userTable: UserTable,
) : UserRepository {

    override suspend fun getUserByEmail(email: String): Response<Any> {
        val user = userTable.getUserByEmail(email)
        return if (user == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = BaseResponse(
                    isSuccess = false,
                    message = "User with email '$email' not exist"
                )
            )
        } else {
            Response.Success(
                code = HttpStatusCode.OK,
                data = user.toUserInfoResponse()
            )
        }
    }
}