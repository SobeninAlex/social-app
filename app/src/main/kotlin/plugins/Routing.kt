package plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import repository.AuthRepository
import repository.FollowersRepository
import repository.UserRepository
import route.authRouting
import route.followsRouting
import route.userRouting

fun Application.configureRouting() {
    val authRepository by inject<AuthRepository>()
    val userRepository by inject<UserRepository>()
    val followerRepository by inject<FollowersRepository>()

    routing {
        authRouting(authRepository)
        userRouting(userRepository)
        followsRouting(followerRepository)
    }
}
