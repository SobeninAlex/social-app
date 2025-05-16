package post_comments

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import post.PostTable
import user.UserTable

internal object PostCommentsTable : Table(name = "post_comments") {

    val commentId = text(name = "comment_id").uniqueIndex()
    val postId = text(name = "post_id").references(ref = PostTable.postId, onDelete = ReferenceOption.CASCADE)
    val userId = text(name = "user_id").references(ref = UserTable.userId, ReferenceOption.CASCADE)
    val content = varchar(name = "content", length = 500)
    val createdAt = datetime(name = "created_at").defaultExpression(defaultValue = CurrentDateTime)
}