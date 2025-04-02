package repository

import Response

interface UserRepository {

    suspend fun getUserByEmail(email: String): Response<Any>
}