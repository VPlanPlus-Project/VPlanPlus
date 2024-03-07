package es.jvbabi.vplanplus.shared.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.shared.data.Authentication
import io.ktor.http.HttpMethod

interface NetworkRepository {
    var authentication: Authentication?

    suspend fun doRequest(
        path: String,
        requestMethod: HttpMethod = HttpMethod.Get,
        requestBody: String? = null,
        queries: Map<String, String> = emptyMap()
    ): DataResponse<String?>
}