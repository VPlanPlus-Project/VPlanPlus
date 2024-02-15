package es.jvbabi.vplanplus.domain

import io.ktor.http.HttpStatusCode

data class DataResponse<T>(
    val data: T,
    val response: HttpStatusCode?
)

@Deprecated("Use HttpStatusCode instead")
enum class Response {
    SUCCESS,
    WRONG_CREDENTIALS,
    NO_INTERNET,
    NONE,
    OTHER,
    NOT_FOUND,
}