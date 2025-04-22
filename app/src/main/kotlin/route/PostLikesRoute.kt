package route

import ErrorMessage
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.request.PostLikeRequest
import model.response.SimpleResponse
import repository.PostLikesRepository

fun Route.postLikesRoute(repository: PostLikesRepository) {
    authenticate {
        route(path = "/post/likes") {
            /** http://127.0.0.1:8080/post/likes/add */
            post(path = "/add") {
                val request = call.receiveNullable<PostLikeRequest>() ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = ErrorMessage.SOMETHING_WRONG
                        )
                    )
                    return@post
                }

                runCatching {
                    repository.addLike(request)
                }.onSuccess { response ->
                    call.respond(
                        status = response.code,
                        message = response.data
                    )
                }.onFailure { error ->
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = error.message ?: ErrorMessage.SOMETHING_WRONG
                        )
                    )
                }
            }

            /** http://127.0.0.1:8080/post/likes/remove */
            post(path = "/remove") {
                val request = call.receiveNullable<PostLikeRequest>() ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = ErrorMessage.SOMETHING_WRONG
                        )
                    )
                    return@post
                }

                runCatching {
                    repository.removeLike(request)
                }.onSuccess { response ->
                    call.respond(
                        status = response.code,
                        message = response.data
                    )
                }.onFailure { error ->
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = error.message ?: ErrorMessage.SOMETHING_WRONG
                        )
                    )
                }
            }
        }
    }
}