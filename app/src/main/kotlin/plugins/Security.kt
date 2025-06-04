package plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.util.*
import model.response.AuthResponse
import org.koin.ktor.ext.inject
import user.UserDao
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val jwtAudience = System.getenv("jwt-audience")
private val jwtIssuer = System.getenv("jwt-domain")
private val jwtSecret = System.getenv("jwt-secret")
private val algorithm = Algorithm.HMAC256(jwtSecret)
private const val CLAIM = "email"

fun Application.configureSecurity() {
    val userDao by inject<UserDao>()

    authentication {
        jwt {
            verifier(
                JWT.require(algorithm)
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            
            validate { credential ->
                if (credential.payload.getClaim(CLAIM).asString() != null) {
                    val userExist = userDao.findByEmail(
                        email = credential.payload.getClaim(CLAIM).asString()
                    ) != null
                    val isValidAudience = credential.payload.audience.contains(jwtAudience)
                    if (userExist && isValidAudience) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            
            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = AuthResponse(
                        errorMessage = "Token is not valid or expired",
                    )
                )
            }
        }
    }
}

fun generateToken(email: String): String {
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim(CLAIM, email)
        .withExpiresAt(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC))
        .sign(algorithm)
}