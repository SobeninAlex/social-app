package route

import ErrorMessage
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import repository.UserRepository

fun Route.userRouting(userRepository: UserRepository) {
    get("/user") {
        val email = call.request.queryParameters["email"] ?: run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "email query parameter is required"
            )
            return@get
        }

        runCatching {
            userRepository.getUserByEmail(email)
        }.onSuccess { response ->
            call.respond(
                status = response.code,
                message = response.data
            )
        }.onFailure { error ->
            call.respond(
                status = HttpStatusCode.Conflict,
                message = error.message ?: ErrorMessage.SOMETHING_WRONG
            )
        }
    }
}