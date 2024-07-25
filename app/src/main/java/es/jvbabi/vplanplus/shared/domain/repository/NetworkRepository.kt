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
        queries: Map<String, String> = emptyMap(),
        onUploading: (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> },
        onDownloading: (bytesReceivedTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): DataResponse<String?>

    suspend fun doRequestRaw(
        path: String,
        requestMethod: HttpMethod = HttpMethod.Get,
        requestBody: Any? = null,
        queries: Map<String, String> = emptyMap(),
        onUploading: (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> },
        onDownloading: (bytesReceivedTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): DataResponse<ByteArray?>

    suspend fun doRequestForm(
        path: String,
        requestMethod: HttpMethod = HttpMethod.Get,
        form: Map<String, String>,
        queries: Map<String, String> = emptyMap(),
        onUploading: (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> },
        onDownloading: (bytesReceivedTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): DataResponse<String?>
}