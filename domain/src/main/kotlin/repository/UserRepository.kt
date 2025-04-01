package repository

import Response
import model.AuthResponse
import model.SingInParams
import model.SingUpParams

interface UserRepository {

    suspend fun signUp(params: SingUpParams): Response<AuthResponse>

    suspend fun signIn(params: SingInParams): Response<AuthResponse>
}