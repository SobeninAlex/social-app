import io.ktor.http.*

sealed class Response<T>(
    val code: HttpStatusCode = HttpStatusCode.OK,
    val data: T,
) {

    class Success<T>(code: HttpStatusCode, data: T) : Response<T>(code, data)

    class Error<T>(code: HttpStatusCode, data: T) : Response<T>(code, data)
}