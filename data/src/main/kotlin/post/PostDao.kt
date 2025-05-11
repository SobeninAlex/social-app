package post

import DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import user.UserTable
import java.util.*

class PostDao {

    suspend fun createPost(caption: String, imageUrl: String, userId: String): Boolean {
        return dbQuery {
            PostTable.insert {
                it[this.postId] = UUID.randomUUID().toString()
                it[this.caption] = caption
                it[this.imageUrl] = imageUrl
                it[this.userId] = userId
            }
                .resultedValues
                ?.singleOrNull() != null
        }
    }

    suspend fun getFeedPosts(follows: List<String>, pageNumber: Int, pageSize: Int): List<PostRow> {
        return dbQuery {
            if (follows.size > 1) {
                getPosts(userIDs = follows, pageNumber = pageNumber, pageSize = pageSize)
            } else {
                PostTable
                    .join(
                        otherTable = UserTable,
                        onColumn = PostTable.userId,
                        otherColumn = UserTable.userId,
                        joinType = JoinType.INNER
                    )
                    .selectAll()
                    .where { PostTable.userId eq follows.first() }
                    .orderBy(column = PostTable.likesCount, order = SortOrder.DESC)
                    .limit(pageSize)
                    .offset((pageNumber * pageSize).toLong())
                    .map { it.toPostRow() }
            }
        }
    }

    suspend fun getPostsByUserId(userId: String, pageNumber: Int, pageSize: Int): List<PostRow> {
        return dbQuery {
            getPosts(userIDs = listOf(userId), pageNumber = pageNumber, pageSize = pageSize)
        }
    }

    suspend fun getPost(postId: String, userId: String): PostRow? {
        return dbQuery {
            PostTable
                .join(
                    otherTable = UserTable,
                    onColumn = PostTable.userId,
                    otherColumn = UserTable.userId,
                    joinType = JoinType.INNER
                )
                .selectAll()
                .where { (PostTable.postId eq postId) and (PostTable.userId eq userId) }
                .map { it.toPostRow() }
                .singleOrNull()

        }
    }

    suspend fun getPost(postId: String): PostRow? {
        return dbQuery {
            PostTable
                .join(
                    otherTable = UserTable,
                    onColumn = PostTable.userId,
                    otherColumn = UserTable.userId,
                    joinType = JoinType.INNER
                )
                .selectAll()
                .where { (PostTable.postId eq postId)}
                .map { it.toPostRow() }
                .singleOrNull()

        }
    }

    suspend fun deletePost(postId: String): Boolean {
        return dbQuery {
            PostTable.deleteWhere { PostTable.postId eq postId } > 0
        }
    }

    suspend fun updateLikesCount(postId: String, decrement: Boolean = false): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1
            PostTable.update(
                where = { PostTable.postId eq postId },
            ) {
                it.update(column = likesCount, value = likesCount.plus(value))
            } > 0
        }
    }

    suspend fun updateCommentsCount(postId: String, decrement: Boolean = false): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1
            PostTable.update(
                where = { PostTable.postId eq postId },
            ) {
                it.update(column = commentsCount, value = commentsCount.plus(value))
            } > 0
        }
    }

    private fun getPosts(userIDs: List<String>, pageNumber: Int, pageSize: Int): List<PostRow> {
        return PostTable
            .join(
                otherTable = UserTable,
                onColumn = PostTable.userId,
                otherColumn = UserTable.userId,
                joinType = JoinType.INNER
            )
            .selectAll()
            .where { PostTable.userId inList userIDs }
            .orderBy(column = PostTable.likesCount, order = SortOrder.DESC)
            .limit(pageSize)
            .offset((pageNumber * pageSize).toLong())
            .map { it.toPostRow() }
    }

    private fun ResultRow.toPostRow(): PostRow {
        return PostRow(
            postId = this[PostTable.postId],
            caption = this[PostTable.caption],
            imageUrl = this[PostTable.imageUrl],
            createdAt = this[PostTable.createdAt].toString(),
            likesCount = this[PostTable.likesCount],
            commentsCount = this[PostTable.commentsCount],
            userId = this[PostTable.userId],
            userName = this[UserTable.userName],
            userAvatar = this[UserTable.userAvatar],
        )
    }
}