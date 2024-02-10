package es.jvbabi.vplanplus.domain

data class DataResponse<T>(
    val data: T,
    val response: Response
)

enum class Response {
    SUCCESS,
    WRONG_CREDENTIALS,
    NO_INTERNET,
    NONE,
    OTHER,
    NOT_FOUND,
    NO_DATA_AVAILABLE
}