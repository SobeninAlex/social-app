package route

import Constants
import ErrorMessage
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import model.request.PostTextRequest
import model.response.SimpleResponse
import repository.PostRepository
import saveFile
import java.io.File

fun Route.postsRoute(repository: PostRepository) {
    authenticate {
        route(path = "/post") {
            /** http://127.0.0.1:8080/post/create */
            post(path = "/create") {
                var fileName = ""
                var postTextRequest: PostTextRequest? = null
                val multipart = call.receiveMultipart()

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.saveFile(folderPath = Paths.POST_IMAGES_FOLDER_PATH)
                        }

                        is PartData.FormItem -> {
                            if (part.name == "post_data") {
                                postTextRequest = Json.decodeFromString(part.value)
                            }
                        }

                        else -> {}
                    }
                    part.dispose()
                }

                val imageUrl = "${Constants.BASE_URL}${Paths.POST_IMAGES_FOLDER}$fileName"

                if (postTextRequest == null) {
                    File("${Paths.POST_IMAGES_FOLDER_PATH}/$fileName").delete()

                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(isSuccess = false, errorMessage = "Couldn't parse post data")
                    )
                } else {
                     try {
                         val result = repository.createPost(imageUrl, postTextRequest!!)

                         call.respond(
                             status = result.code,
                             message = result.data
                         )
                     } catch (ex: Exception) {
                         call.respond(
                             status = HttpStatusCode.Conflict,
                             message = SimpleResponse(
                                 isSuccess = false,
                                 errorMessage = ex.message ?: ErrorMessage.SOMETHING_WRONG
                             )
                         )
                     }
                }
            }

            /** http://127.0.0.1:8080/post/{post_id}?current_user_id= */
            get(path = "/{post_id}") {
                val postId = call.parameters["post_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = "parameter post_id is required"
                        )
                    )
                    return@get
                }

                val userId = call.request.queryParameters["user_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = "parameter user_id is required"
                        )
                    )
                    return@get
                }

                runCatching {
                    repository.getPost(postId = postId, userId = userId)
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

            /** http://127.0.0.1:8080/post/{post_id} */
            delete(path = "/{post_id}") {
                val postId = call.parameters["post_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = "parameter post_id is required"
                        )
                    )
                    return@delete
                }

                runCatching {
                    repository.deletePost(postId = postId)
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

        route(path = "/posts") {
            /** http://127.0.0.1:8080/posts/feed?user_id=&page=&page_size= */
            get(path = "/feed") {
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

                val pageNumber = call.request.queryParameters["page"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                runCatching {
                    repository.getFeedPosts(
                        userId = userId,
                        pageNumber = pageNumber,
                        pageSize = pageSize,
                    )
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

            /** http://127.0.0.1:8080/posts/{user_id}?current_user_id=&page=&page_size= */
            get(path = "/{user_id}") {
                val userId = call.parameters["user_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = "parameter user_id is required"
                        )
                    )
                    return@get
                }

                val currentUserId = call.request.queryParameters["current_user_id"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = "query parameter current_user_id is required"
                        )
                    )
                    return@get
                }

                val pageNumber = call.request.queryParameters["page"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                runCatching {
                    repository.getPostsByUser(
                        userId = userId,
                        currentUserId = currentUserId,
                        pageNumber = pageNumber,
                        pageSize = pageSize,
                    )
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