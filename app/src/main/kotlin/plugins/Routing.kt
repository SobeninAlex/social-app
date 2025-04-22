package plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import repository.*
import route.*

fun Application.configureRouting() {
    val authRepository by inject<AuthRepository>()
    val userRepository by inject<UserRepository>()
    val followerRepository by inject<FollowersRepository>()
    val postRepository by inject<PostRepository>()
    val postCommentsRepository by inject<PostCommentsRepository>()

    routing {
        authRouting(authRepository)
        userRouting(userRepository)
        followsRouting(followerRepository)
        postsRoute(postRepository)
        postCommentsRoute(postCommentsRepository)

        staticResources("/", "static")
    }
}
