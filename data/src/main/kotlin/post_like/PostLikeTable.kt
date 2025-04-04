package post_like

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import post.PostTable
import user.UserTable

internal object PostLikeTable : Table(name = "post_like") {

    val likeId = text("like_id").uniqueIndex()
    val postId = text("post_id").references(ref = PostTable.postId, onDelete = ReferenceOption.CASCADE)
    val userId = text(name = "user_id").references(ref = UserTable.userId, onDelete = ReferenceOption.CASCADE)
    val likeDate = datetime("like_date").defaultExpression(defaultValue = CurrentDateTime)
}