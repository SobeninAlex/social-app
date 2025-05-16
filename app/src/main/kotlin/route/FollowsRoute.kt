package route

import ErrorMessage
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.request.FollowRequest
import model.response.SimpleResponse
import repository.FollowersRepository

fun Route.followsRouting(repository: FollowersRepository) {
    authenticate {
        route(path = "/follows") {
            /** http://127.0.0.1:8080/follows/follow */
            post(path = "/follow") {
                val request = call.receiveNullable<FollowRequest>() ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = ErrorMessage.SOMETHING_WRONG
                        )
                    )
                    return@post
                }

                try {
                    val result = repository.followUser(
                        follower = request.follower,
                        following = request.following
                    )

                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (ex: Exception) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = ex.message ?: ErrorMessage.SOMETHING_WRONG
                    )
                }
            }

            /** http://127.0.0.1:8080/follows/unfollow */
            post(path = "/unfollow") {
                val request = call.receiveNullable<FollowRequest>() ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = ErrorMessage.SOMETHING_WRONG
                        )
                    )
                    return@post
                }

                try {
                    val result = repository.unfollowUser(
                        follower = request.follower,
                        following = request.following
                    )

                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (ex: Exception) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = ex.message ?: ErrorMessage.SOMETHING_WRONG
                    )
                }
            }

            /** http://127.0.0.1:8080/follows/followers?user_id=&page=&page_size= */
            get(path = "/followers") {
                val userId = call.request.queryParameters["user_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = "query parameter user_id is required"
                        )
                    )
                    return@get
                }

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                runCatching {
                    repository.getFollowers(
                        userId = userId,
                        page = page,
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
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = error.message ?: ErrorMessage.SOMETHING_WRONG
                        )
                    )
                }
            }

            /** http://127.0.0.1:8080/follows/following?user_id=&page=&page_size= */
            get(path = "/following") {
                val userId = call.request.queryParameters["user_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = "query parameter user_id is required"
                        )
                    )
                    return@get
                }

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                runCatching {
                    repository.getFollowing(
                        userId = userId,
                        page = page,
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
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = error.message ?: ErrorMessage.SOMETHING_WRONG
                        )
                    )
                }
            }

            /** http://127.0.0.1:8080/follows/suggestions?user_id= */
            get(path = "/suggestions") {
                val userId = call.request.queryParameters["user_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = "query parameter user_id is required"
                        )
                    )
                    return@get
                }

                runCatching {
                    repository.getFollowingSuggestions(userId = userId)
                }.onSuccess { response ->
                    call.respond(
                        status = response.code,
                        message = response.data
                    )
                }.onFailure { error ->
                    call.respond(
                        status = HttpStatusCode.BadRequest,
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