package follows

import DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import follows.FollowsTable.followDate
import follows.FollowsTable.followerId
import follows.FollowsTable.followingId

class FollowsDao {

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
                .where { followingId eq userId }
                .orderBy(followDate, SortOrder.DESC)
                .limit(count = pageSize)
                .offset(start = (pageNumber * pageSize).toLong())
                .map { it[followerId] }
        }
    }

    suspend fun getFollowing(userId: String, pageNumber: Int, pageSize: Int): List<String> {
        return dbQuery {
            FollowsTable.selectAll()
                .where { followerId eq userId }
                .orderBy(followDate, SortOrder.DESC)
                .limit(count = pageSize)
                .offset(start = (pageNumber * pageSize).toLong())
                .map { it[followingId] }
        }
    }

    suspend fun getAllFollowing(userId: String): List<String> {
        return dbQuery {
            FollowsTable.selectAll()
                .where { followerId eq userId }
                .map { it[followingId] }
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
