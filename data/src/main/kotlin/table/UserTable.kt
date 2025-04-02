package table

import DatabaseFactory.dbQuery
import hashPassword
import model.dto.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import java.util.UUID

object UserTable: Table(name = "users") {

    private val userId = text(name = "user_id")
    private val userName = varchar(name = "user_name", length = 255)
    private val userEmail = varchar(name = "user_email", length = 255)
    private val userBio = text(name = "user_bio").default("")
    private val userPassword = varchar(name = "user_password", length = 100)
    private val userAvatar = text(name = "user_avatar").nullable()
    private val followersCount = integer("followers_count").default(0)
    private val followingCount = integer("following_count").default(0)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userId)

    suspend fun insert(user: User): User? {
        return dbQuery {
            UserTable.insert {
                it[userId] = UUID.randomUUID().toString()
                it[userName] = user.name
                it[userEmail] = user.email
                it[userBio] = user.bio
                it[userPassword] = hashPassword(user.password)
                it[userAvatar] = user.avatar
                it[followersCount] = user.followersCount
                it[followingCount] = user.followingCount
            }
                .resultedValues
                ?.firstOrNull()
                ?.toUser()
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return dbQuery {
            UserTable.selectAll()
                .where { userEmail eq email }
                .map { it.toUser() }
                .singleOrNull()
        }
    }

    suspend fun updateFollowsCount(
        follower: String,
        following: String,
        isFollowing: Boolean
    ): Boolean {
        return dbQuery {
            val count = if (isFollowing) +1 else -1

            val successFollower = UserTable.update(
                where = { userId eq follower }
            ) {
                it.update(
                    column = followingCount,
                    value = followingCount.plus(count)
                )
            } > 0

            val successFollowing = UserTable.update(
                where = { userId eq following }
            ) {
                it.update(
                    column = followersCount,
                    value = followersCount.plus(count)
                )
            } > 0

            successFollower && successFollowing
        }
    }

    private fun ResultRow.toUser(): User {
        return User(
            id = this[userId],
            name = this[userName],
            email = this[userEmail],
            bio = this[userBio],
            avatar = this[userAvatar],
            password = this[userPassword],
            followersCount = this[followersCount],
            followingCount = this[followingCount],
        )
    }
}