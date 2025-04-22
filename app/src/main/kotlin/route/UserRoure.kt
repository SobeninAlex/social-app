package route

import ErrorMessage
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.*
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import model.request.UpdateUserRequest
import model.response.SimpleResponse
import repository.UserRepository
import saveFile
import java.io.File

fun Route.userRouting(userRepository: UserRepository) {
    authenticate {
        route(path = "/user") {
            /** http://127.0.0.1:8080/user?email= */
            get {
                val email = call.request.queryParameters["email"] ?: run {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = "query parameter email is required"
                        )
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
                        message = SimpleResponse(
                            isSuccess = false,
                            errorMessage = error.message ?: ErrorMessage.SOMETHING_WRONG
                        )
                    )
                }
            }

            /** http://127.0.0.1:8080/user/{user_id}?current_user_id= */
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

                runCatching {
                    userRepository.getUserById(id = userId, currentUserId = currentUserId)
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

            /** http://127.0.0.1:8080/user/update */
            post(path = "/update") {
                var fileName = ""
                var updateUserRequest: UpdateUserRequest? = null
                val multipart = call.receiveMultipart()

                try {
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                fileName = part.saveFile(folderPath = Paths.USER_IMAGES_FOLDER_PATH)
                            }

                            is PartData.FormItem -> {
                                if (part.name == "user_data") {
                                    updateUserRequest = Json.decodeFromString(part.value)
                                }
                            }

                            else -> {}
                        }
                        part.dispose()
                    }

                    val imageUrl = "${Constants.BASE_URL}${Paths.USER_IMAGES_FOLDER}$fileName"

                    val result = userRepository.updateUser(
                        updateUserRequest = updateUserRequest!!.copy(
                            avatar = if (fileName.isNotEmpty()) imageUrl else updateUserRequest!!.avatar,
                        )
                    )

                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (ex: Exception) {
                    if (fileName.isNotEmpty()) {
                        File("${Paths.USER_IMAGES_FOLDER_PATH}/$fileName").delete()
                    }

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
    }
}