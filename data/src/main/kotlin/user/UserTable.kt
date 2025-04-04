package user

import org.jetbrains.exposed.sql.Table

internal object UserTable : Table(name = "users") {

    val userId = text(name = "user_id").uniqueIndex()
    val userName = varchar(name = "user_name", length = 255)
    val userEmail = varchar(name = "user_email", length = 255)
    val userBio = text(name = "user_bio").default("")
    val userPassword = varchar(name = "user_password", length = 100)
    val userAvatar = text(name = "user_avatar").nullable()
    val followersCount = integer("followers_count").default(0)
    val followingCount = integer("following_count").default(0)

    override val primaryKey: PrimaryKey get() = PrimaryKey(userId)
}

