package plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import repository.UserRepository
import route.authRouting

fun Application.configureRouting() {
    val userRepository by inject<UserRepository>()

    routing {
        authRouting(userRepository)
    }
}
