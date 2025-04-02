package table

import DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object FollowsTable : Table(name = "follows") {

    private val followerId = text("follower_id")
    private val followingId = text("following_id")
    private val followDate = datetime(name = "follow_date").defaultExpression(CurrentDateTime)

    suspend fun followUser(
        follower: String,
        following: String,
    ): Boolean {
        return dbQuery {
            FollowsTable.insert {
                it[followerId] = follower
                it[followingId] = following
            }
                .resultedValues
                ?.singleOrNull() != null
        }
    }

    suspend fun unfollowUser(
        follower: String,
        following: String,
    ) : Boolean {
        return dbQuery {
            FollowsTable.deleteWhere {
                (followerId eq follower) and (followingId eq following)
            } > 0
        }
    }

    suspend fun getFollowers(userId: String, pageNumber: Int, pageSize: Int): List<String> {
        return dbQuery {
            FollowsTable.selectAll()
                .where { followerId eq userId }
                .orderBy(followDate, SortOrder.DESC)
                .limit(count = pageSize)
                .offset(start = ((pageNumber - 1) * pageSize).toLong())
                .map { it[followingId] }
        }
    }

    suspend fun getFollowing(userId: String, pageNumber: Int, pageSize: Int): List<String> {
        return dbQuery {
            FollowsTable.selectAll()
                .where { followingId eq userId }
                .orderBy(followDate, SortOrder.DESC)
                .limit(count = pageSize)
                .offset(start = ((pageNumber - 1) * pageSize).toLong())
                .map { it[followerId] }
        }
    }

    suspend fun isAlreadyFollowing(follower: String, following: String): Boolean {
        return dbQuery {
            FollowsTable.selectAll()
                .where { (followerId eq follower) and (followingId eq following) }
                .toList()
                .isNotEmpty()
        }
    }
}