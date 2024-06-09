package es.jvbabi.vplanplus.shared.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.shared.data.Authentication
import io.ktor.http.HttpMethod

interface NetworkRepository {
    var authentication: Authentication?

    suspend fun doRequest(
        path: String,
        requestMethod: HttpMethod = HttpMethod.Get,
        requestBody: Any? = null,
        queries: Map<String, String> = emptyMap()
    ): DataResponse<String?>

    suspend fun doRequestRaw(
        path: String,
        requestMethod: HttpMethod = HttpMethod.Get,
        requestBody: Any? = null,
        queries: Map<String, String> = emptyMap()
    ): DataResponse<ByteArray?>
}