package route

import ErrorMessage
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.response.AuthResponse
import model.request.SingInRequest
import model.request.SingUpRequest
import repository.AuthRepository

fun Route.authRouting(repository: AuthRepository) {
    post("/signup") {
        val request = call.receiveNullable<SingUpRequest>() ?: run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = AuthResponse(
                    errorMessage = ErrorMessage.SOMETHING_WRONG,
                )
            )
            return@post
        }

        try {
            val result = repository.signUp(request)
            call.respond(
                status = result.code,
                message = result.data
            )
        } catch (ex: Exception) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = AuthResponse(
                    errorMessage = ex.message ?: ErrorMessage.SOMETHING_WRONG,
                )
            )
        }
    }

    post("/signin") {
        val request = call.receiveNullable<SingInRequest>() ?: run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = AuthResponse(
                    errorMessage = ErrorMessage.SOMETHING_WRONG,
                )
            )
            return@post
        }

        try {
            val result = repository.signIn(request)
            call.respond(
                status = result.code,
                message = result.data
            )
        } catch (ex: Exception) {
            call.respond(
                status = HttpStatusCode.Conflict,
                message = AuthResponse(
                    errorMessage = ex.message ?: ErrorMessage.SOMETHING_WRONG,
                )
            )
        }
    }
}