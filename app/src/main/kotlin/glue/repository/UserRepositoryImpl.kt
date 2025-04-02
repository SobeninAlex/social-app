package glue.repository

import Response
import io.ktor.http.*
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
                data = "User with $email email not exist"
            )
        } else {
            Response.Success(
                code = HttpStatusCode.OK,
                data = user
            )
        }
    }
}