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

object ResponseFactory {
    fun getResponse(responseCode: Int): Response {
        return when (responseCode) {
            200 -> Response.SUCCESS
            401 -> Response.WRONG_CREDENTIALS
            404 -> Response.NOT_FOUND
            else -> Response.OTHER
        }
    }
}