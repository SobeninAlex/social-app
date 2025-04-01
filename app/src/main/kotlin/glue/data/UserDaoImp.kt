package glue.data

import DatabaseFactory.dbQuery
import model.SingUpParams
import model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import user.UserDao
import user.UserRow

class UserDaoImp : UserDao {

    override suspend fun insert(params: SingUpParams): User? {
        return dbQuery {
            UserRow.insert {
                it[userName] = params.name
                it[userPassword] = params.password
                it[userEmail] = params.email
            }
                .resultedValues
                ?.firstOrNull()
                ?.toUser()
        }
    }

    override suspend fun findUserByEmail(email: String): User? {
        return dbQuery {
            UserRow.selectAll()
                .where { UserRow.userEmail eq email }
                .map { it.toUser() }
                .singleOrNull()
        }
    }

    private fun ResultRow.toUser(): User {
        return User(
            id = this[UserRow.userId],
            name = this[UserRow.userName],
            email = this[UserRow.userEmail],
            bio = this[UserRow.userBio],
            avatar = this[UserRow.userAvatar],
            password = this[UserRow.userPassword],
        )
    }
}