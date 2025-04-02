package repository

import Response
import model.response.AuthResponse
import model.request.SingInRequest
import model.request.SingUpRequest

interface AuthRepository {

    suspend fun signUp(params: SingUpRequest): Response<AuthResponse>

    suspend fun signIn(params: SingInRequest): Response<AuthResponse>
}