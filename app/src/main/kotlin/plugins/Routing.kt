package plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import repository.AuthRepository
import repository.FollowersRepository
import repository.PostRepository
import repository.UserRepository
import route.authRouting
import route.followsRouting
import route.postsRoute
import route.userRouting

fun Application.configureRouting() {
    val authRepository by inject<AuthRepository>()
    val userRepository by inject<UserRepository>()
    val followerRepository by inject<FollowersRepository>()
    val postRepository by inject<PostRepository>()

    routing {
        authRouting(authRepository)
        userRouting(userRepository)
        followsRouting(followerRepository)
        postsRoute(postRepository)

        staticResources("/", "static")
    }
}
