package route

import ErrorMessage
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.request.FollowRequest
import model.response.FollowAndUnfollowResponse
import repository.FollowersRepository

fun Route.followsRouting(repository: FollowersRepository) {
    authenticate {
        post("/follow") {
            val request = call.receiveNullable<FollowRequest>() ?: run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = FollowAndUnfollowResponse(
                        isSuccess = false,
                        message = ErrorMessage.SOMETHING_WRONG
                    )
                )
                return@post
            }

            try {
                val result = if (request.isFollowing) {
                    repository.followUser(
                        follower = request.follower,
                        following = request.following
                    )
                } else {
                    repository.unfollowUser(
                        follower = request.follower,
                        following = request.following
                    )
                }

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
    }
}