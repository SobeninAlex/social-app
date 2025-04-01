package user

import model.SingUpParams
import model.User

interface UserDao {

    suspend fun insert(params: SingUpParams): User?

    suspend fun findUserByEmail(email: String): User?
}