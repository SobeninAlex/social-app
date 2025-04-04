package repository

import Response
import model.response.InfoUserResponse

interface UserRepository {

    suspend fun getUserByEmail(email: String): Response<InfoUserResponse>
}