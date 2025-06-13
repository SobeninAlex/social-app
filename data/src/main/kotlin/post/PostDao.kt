package post

import DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import user.UserTable
import java.util.*

class PostDao {

    suspend fun createPost(caption: String, imageUrls: List<String>, userId: String): PostRow? {
        return dbQuery {
            val user = UserTable.selectAll()
                .where { UserTable.userId eq userId }
                .first()

            PostTable.insert {
                it[this.postId] = UUID.randomUUID().toString()
                it[this.caption] = caption
                it[this.imageUrls] = imageUrls
                it[this.userId] = userId
            }
                .resultedValues
                ?.singleOrNull()
                ?.toNewPostRow(
                    userName = user[UserTable.userName],
                    userAvatar = user[UserTable.userAvatar],
                )
        }
    }

    suspend fun getFeedPosts(follows: List<String>, page: Int, pageSize: Int): List<PostRow> {
        return dbQuery {
            if (follows.size > 1) {
                getPosts(userIDs = follows, page = page, pageSize = pageSize)
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
                    .limit(pageSize)
                    .offset((page * pageSize).toLong())
                    .orderBy(column = PostTable.createdAt, order = SortOrder.DESC)
                    .map { it.toPostRow() }
            }
        }
    }

    suspend fun getPostsByUserId(userId: String, page: Int, pageSize: Int): List<PostRow> {
        return dbQuery {
            getPosts(userIDs = listOf(userId), page = page, pageSize = pageSize)
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

    private fun getPosts(userIDs: List<String>, page: Int, pageSize: Int): List<PostRow> {
        return PostTable
            .join(
                otherTable = UserTable,
                onColumn = PostTable.userId,
                otherColumn = UserTable.userId,
                joinType = JoinType.INNER
            )
            .selectAll()
            .where { PostTable.userId inList userIDs }
            .limit(pageSize)
            .offset((page * pageSize).toLong())
            .orderBy(column = PostTable.createdAt, order = SortOrder.DESC)
            .map { it.toPostRow() }
    }

    private fun ResultRow.toPostRow(): PostRow {
        return PostRow(
            postId = this[PostTable.postId],
            caption = this[PostTable.caption],
            imageUrls = this[PostTable.imageUrls],
            createdAt = this[PostTable.createdAt].toString(),
            likesCount = this[PostTable.likesCount],
            commentsCount = this[PostTable.commentsCount],
            userId = this[PostTable.userId],
            userName = this[UserTable.userName],
            userAvatar = this[UserTable.userAvatar],
        )
    }

    private fun ResultRow.toNewPostRow(userName: String, userAvatar: String?): PostRow {
        return PostRow(
            postId = this[PostTable.postId],
            caption = this[PostTable.caption],
            imageUrls = this[PostTable.imageUrls],
            createdAt = this[PostTable.createdAt].toString(),
            likesCount = this[PostTable.likesCount],
            commentsCount = this[PostTable.commentsCount],
            userId = this[PostTable.userId],
            userName = userName,
            userAvatar = userAvatar,
        )
    }
}