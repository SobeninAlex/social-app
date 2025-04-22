package user

import DatabaseFactory.dbQuery
import hashPassword
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import user.UserTable.followersCount
import user.UserTable.followingCount
import user.UserTable.userAvatar
import user.UserTable.userBio
import user.UserTable.userEmail
import user.UserTable.userId
import user.UserTable.userName
import user.UserTable.userPassword
import java.util.*

class UserDao {

    suspend fun insert(user: UserRow): UserRow? {
        return dbQuery {
            UserTable.insert {
                it[userId] = UUID.randomUUID().toString()
                it[userName] = user.userName
                it[userEmail] = user.userEmail
                it[userBio] = user.userBio
                it[userPassword] = hashPassword(user.userPassword)
                it[userAvatar] = user.userAvatar
                it[followersCount] = user.followersCount
                it[followingCount] = user.followingCount
            }
                .resultedValues
                ?.firstOrNull()
                ?.toUserRow()
        }
    }

    suspend fun findByEmail(email: String): UserRow? {
        return dbQuery {
            UserTable.selectAll()
                .where { userEmail eq email }
                .map { it.toUserRow() }
                .singleOrNull()
        }
    }

    suspend fun findById(id: String): UserRow? {
        return dbQuery {
            UserTable.selectAll()
                .where { userId eq id }
                .map { it.toUserRow() }
                .singleOrNull()
        }
    }

    suspend fun update(id: String, name: String, bio: String, avatar: String?): Boolean {
        return dbQuery {
            UserTable.update(
                where = { userId eq id },
            ) {
                it[userName] = name
                it[userBio] = bio
                it[userAvatar] = avatar
            } > 0
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

    suspend fun getUsers(ids: List<String>): List<UserRow> {
        return dbQuery {
            UserTable.selectAll()
                .where { userId inList ids }
                .map { it.toUserRow() }
        }
    }

    suspend fun getPopularUsers(limit: Int): List<UserRow> {
        return dbQuery {
            UserTable.selectAll()
                .orderBy(column = followersCount, order = SortOrder.DESC)
                .limit(count = limit)
                .map { it.toUserRow() }
        }
    }

    private fun ResultRow.toUserRow(): UserRow {
        return UserRow(
            userId = this[userId],
            userName = this[userName],
            userEmail = this[userEmail],
            userBio = this[userBio],
            userAvatar = this[userAvatar],
            userPassword = this[userPassword],
            followersCount = this[followersCount],
            followingCount = this[followingCount],
        )
    }
}