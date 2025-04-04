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
import model.response.BaseResponse
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
                val multipartPart = call.receiveMultipart()

                multipartPart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.saveFile(folderPath = Constants.POST_IMAGES_FOLDER_PATH)
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

                val imageUrl = "${Constants.BASE_URL}${Constants.POST_IMAGES_FOLDER}$fileName"

                if (postTextRequest == null) {
                    File("${Constants.POST_IMAGES_FOLDER_PATH}/$fileName").delete()

                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseResponse(isSuccess = false, errorMessage = "Couldn't parse post data")
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
                             message = BaseResponse(
                                 isSuccess = false,
                                 errorMessage = ex.message ?: ErrorMessage.SOMETHING_WRONG
                             )
                         )
                     }
                }
            }

            /** http://127.0.0.1:8080/post/{post_id}?user_id= */
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

                val userId = call.request.queryParameters["user_id"]

                runCatching {
                    if (userId == null) {
                        repository.getPost(postId = postId)
                    } else {
                        repository.getPost(postId = postId, userId = userId)
                    }
                }.onSuccess { response ->
                    call.respond(
                        status = response.code,
                        message = response.data
                    )
                }.onFailure { error ->
                    call.respond(
                        status = HttpStatusCode.Conflict,
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