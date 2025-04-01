package user

import org.jetbrains.exposed.sql.Table

object UserRow: Table(name = "users") {
    val userId = integer(name = "user_id").autoIncrement()
    val userName = varchar(name = "user_name", length = 255)
    val userEmail = varchar(name = "user_email", length = 255)
    val userBio = text(name = "user_bio").default(
        defaultValue = "Welcome to my page!"
    )
    val userPassword = varchar(name = "user_password", length = 100)
    val userAvatar = text(name = "user_avatar").nullable()

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userId)
}