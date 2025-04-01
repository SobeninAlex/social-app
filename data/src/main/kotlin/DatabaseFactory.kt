import user.UserRow
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private val dbUrl = System.getenv("DB_URL")
    private val dbUser = System.getenv("DB_USER")
    private val dbPassword = System.getenv("DB_PASSWORD")
    private val driver = "org.postgresql.Driver"

    fun initDatabase() {
        Database.connect(getHikariDataSource())

        transaction {
            SchemaUtils.create(
                UserRow
            )
        }
    }

    private fun getHikariDataSource(): HikariDataSource {
        println("DB_URL: $dbUrl")
        println("DB_USER: $dbUser")

        val config = HikariConfig()
        config.apply {
            driverClassName = driver
            jdbcUrl = dbUrl
            username = dbUser
            password = dbPassword
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T {
        return newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}