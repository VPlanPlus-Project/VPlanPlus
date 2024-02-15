package es.jvbabi.vplanplus.domain

import io.ktor.http.HttpStatusCode

data class DataResponse<T>(
    val data: T,
    val response: HttpStatusCode?
)
