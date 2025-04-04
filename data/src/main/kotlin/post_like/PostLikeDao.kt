package post_like

import DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

class PostLikeDao {

    suspend fun addLike(postId: String, userId: String): Boolean {
        return dbQuery {
            PostLikeTable.insert {
                it[likeId] = UUID.randomUUID().toString()
                it[PostLikeTable.postId] = postId
                it[PostLikeTable.userId] = userId
            }
                .resultedValues
                ?.isNotEmpty() ?: false
        }
    }

    suspend fun removeLike(postId: String, userId: String): Boolean {
        return dbQuery {
            PostLikeTable.deleteWhere { (PostLikeTable.postId eq postId) and (PostLikeTable.userId eq userId) } > 0
        }
    }

    suspend fun isPostLikedByUser(postId: String, userId: String): Boolean {
        return dbQuery {
            PostLikeTable.selectAll()
                .where { (PostLikeTable.postId eq postId) and (PostLikeTable.userId eq userId) }
                .toList()
                .isNotEmpty()
        }
    }
}