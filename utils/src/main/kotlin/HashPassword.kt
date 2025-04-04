import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val hashSecretKey = System.getenv("HASH_SECRET_KEY").toByteArray()
private val hmacKey = SecretKeySpec(hashSecretKey, "HmacSHA1")

fun hashPassword(password: String): String {
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}