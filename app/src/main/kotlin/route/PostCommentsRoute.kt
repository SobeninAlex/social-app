package route

import ErrorMessage
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.request.NewCommentRequest
import model.response.BaseResponse
import repository.PostCommentsRepository

fun Route.postCommentsRoute(repository: PostCommentsRepository) {
    authenticate {
        route(path = "/post/comments") {
            /** http://127.0.0.1:8080/post/comments/create */
            post(path = "/create") {
                val request = call.receiveNullable<NewCommentRequest>() ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseResponse(
                            isSuccess = false,
                            errorMessage = ErrorMessage.SOMETHING_WRONG
                        )
                    )
                    return@post
                }

                runCatching {
                    repository.addComment(request)
                }.onSuccess { response ->
                    call.respond(
                        status = response.code,
                        message = response.data
                    )
                }.onFailure { error ->
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseResponse(
                            isSuccess = false,
                            errorMessage = error.message ?: ErrorMessage.SOMETHING_WRONG
                        )
                    )
                }
            }

            /** http://127.0.0.1:8080/post/comments/delete/{comment_id}?user_id=&post_id= */
            delete(path = "/delete/{comment_id}") {
                val commentId = call.parameters["comment_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseResponse(
                            isSuccess = false,
                            errorMessage = "parameter comment_id is required"
                        )
                    )
                    return@delete
                }

                val userId = call.request.queryParameters["user_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseResponse(
                            isSuccess = false,
                            errorMessage = "query parameter user_id is required"
                        )
                    )
                    return@delete
                }

                val postId = call.request.queryParameters["post_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseResponse(
                            isSuccess = false,
                            errorMessage = "query parameter post_id is required"
                        )
                    )
                    return@delete
                }

                runCatching {
                    repository.deleteComment(
                        commentId = commentId,
                        postId = postId,
                        userId = userId
                    )
                }.onSuccess { response ->
                    call.respond(
                        status = response.code,
                        message = response.data
                    )
                }.onFailure { error ->
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseResponse(
                            isSuccess = false,
                            errorMessage = error.message ?: ErrorMessage.SOMETHING_WRONG
                        )
                    )
                }
            }

            /** http://127.0.0.1:8080/post/comments/{post_id}?page_number=&page_size= */
            get(path = "/{post_id}") {
                val postId = call.parameters["post_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseResponse(
                            isSuccess = false,
                            errorMessage = "parameter post_id is required"
                        )
                    )
                    return@get
                }

                val pageNumber = call.request.queryParameters["page_number"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                runCatching {
                    repository.getPostComments(
                        postId = postId,
                        pageNumber = pageNumber,
                        pageSize = pageSize
                    )
                }.onSuccess { response ->
                    call.respond(
                        status = response.code,
                        message = response.data
                    )
                }.onFailure { error ->
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseResponse(
                            isSuccess = false,
                            errorMessage = error.message ?: ErrorMessage.SOMETHING_WRONG
                        )
                    )
                }
            }
        }
    }
}