package follows

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

internal object FollowsTable : Table(name = "follows") {

    val followerId = text("follower_id")
    val followingId = text("following_id")
    val followDate = datetime(name = "follow_date").defaultExpression(CurrentDateTime)
}