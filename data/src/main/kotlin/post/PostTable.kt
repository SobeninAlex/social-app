package post

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import user.UserTable

internal object PostTable : Table(name = "posts") {

    val postId = text(name = "post_id").uniqueIndex()
    val caption = varchar(name = "caption", length = 300)
    val imageUrl = text(name = "image_url")
    val likesCount = integer(name = "likes_count").default(0)
    val commentsCount = integer(name = "comments_count").default(0)
    val userId = text(name = "user_id").references(ref = UserTable.userId, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime(name = "created_at").defaultExpression(defaultValue = CurrentDateTime)
}

