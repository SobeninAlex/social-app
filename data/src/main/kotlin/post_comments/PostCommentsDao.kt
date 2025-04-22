package post_comments

import DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import user.UserTable
import java.util.*

class PostCommentsDao {

    suspend fun addComment(postId: String, userId: String, content: String): PostCommentsRow? {
        return dbQuery {
            val commentId = UUID.randomUUID().toString()
            PostCommentsTable.insert {
                it[this.commentId] = commentId
                it[this.postId] = postId
                it[this.userId] = userId
                it[this.content] = content
            }

            PostCommentsTable
                .join(
                    otherTable = UserTable,
                    onColumn = PostCommentsTable.userId,
                    otherColumn = UserTable.userId,
                    joinType = JoinType.INNER
                )
                .selectAll()
                .where { (PostCommentsTable.postId eq postId) and (PostCommentsTable.commentId eq commentId) }
                .map { it.toPostCommentsRow() }
                .singleOrNull()
        }
    }

    suspend fun deleteComment(commentId: String, postId: String): Boolean {
        return dbQuery {
            PostCommentsTable.deleteWhere {
                (PostCommentsTable.postId eq postId) and (PostCommentsTable.commentId eq commentId)
            } > 0
        }
    }

    suspend fun findComment(commentId: String, postId: String): PostCommentsRow? {
        return dbQuery {
            PostCommentsTable
                .join(
                    otherTable = UserTable,
                    onColumn = PostCommentsTable.userId,
                    otherColumn = UserTable.userId,
                    joinType = JoinType.INNER
                )
                .selectAll()
                .where { (PostCommentsTable.postId eq postId) and (PostCommentsTable.commentId eq commentId) }
                .map { it.toPostCommentsRow() }
                .singleOrNull()
        }
    }

    suspend fun getComments(postId: String, pageNumber: Int, pageSize: Int): List<PostCommentsRow> {
        return dbQuery {
            PostCommentsTable
                .join(
                    otherTable = UserTable,
                    onColumn = PostCommentsTable.userId,
                    otherColumn = UserTable.userId,
                    joinType = JoinType.INNER
                )
                .selectAll()
                .where { PostCommentsTable.postId eq postId }
                .orderBy(column = PostCommentsTable.createdAt, order = SortOrder.DESC)
                .limit(pageSize)
                .offset(((pageNumber - 1) * pageSize).toLong())
                .map { it.toPostCommentsRow() }
        }
    }

    private fun ResultRow.toPostCommentsRow(): PostCommentsRow {
        return PostCommentsRow(
            commentId = this[PostCommentsTable.commentId],
            postId = this[PostCommentsTable.postId],
            userId = this[PostCommentsTable.userId],
            content = this[PostCommentsTable.content],
            userName = this[UserTable.userName],
            avatar = this[UserTable.userAvatar],
            createdAt = this[PostCommentsTable.createdAt].toString()
        )
    }
}