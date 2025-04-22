package repository

import Response
import model.request.UpdateUserRequest
import model.response.InfoUserResponse

interface UserRepository {

    suspend fun getUserByEmail(email: String): Response<InfoUserResponse>

    suspend fun getUserById(id: String, currentUserId: String): Response<InfoUserResponse>

    suspend fun updateUser(updateUserRequest: UpdateUserRequest): Response<InfoUserResponse>
}