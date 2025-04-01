import plugins.configureRouting
import plugins.configureSecurity
import plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import plugins.configureKoin

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureKoin()
    DatabaseFactory.initDatabase()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
