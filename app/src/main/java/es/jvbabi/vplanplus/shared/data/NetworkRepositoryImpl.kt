package es.jvbabi.vplanplus.shared.data

import android.util.Log
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.servers
import es.jvbabi.vplanplus.shared.domain.repository.NetworkRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import io.ktor.util.toByteArray
import io.ktor.utils.io.ByteReadChannel
import java.net.ConnectException
import java.net.UnknownHostException

const val DEFAULT_USER_AGENT = "VPlanPlus"
const val LOG_CONTENT_ON_ERROR = false

open class NetworkRepositoryImpl(
    server: String,
    private val userAgent: String = DEFAULT_USER_AGENT,
    private val logRepository: LogRecordRepository?,
    init: (NetworkRepositoryImpl.() -> Unit) = {}
) : NetworkRepository {

    private var server: String
    val globalHeaders: MutableMap<String, String> = mutableMapOf()
    override var authentication: Authentication? = null

    init {
        this.server = server
        if (server.endsWith("/")) this.server = server.dropLast(1)
        globalHeaders["App-Version"] = BuildConfig.VERSION_CODE.toString()
        init()
    }

    private val client = HttpClient(Android) {
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 10000
        }
        install(UserAgent) {
            agent = userAgent
        }
    }

    override suspend fun doRequest(
        path: String,
        requestMethod: HttpMethod,
        requestBody: Any?,
        queries: Map<String, String>,
        onUploading: (bytesSentTotal: Long, contentLength: Long) -> Unit,
        onDownloading: (bytesReceivedTotal: Long, contentLength: Long) -> Unit
    ): DataResponse<String?> {
        try {
            logRepository?.log("Network", "Requesting ${requestMethod.value} $server$path")
            val response = client.request("$server$path") request@{
                method = requestMethod
                headers headers@{
                    if (authentication != null) {
                        val (key, value) = authentication!!.toHeader()
                        append(key, value)
                    }
                    globalHeaders.forEach { (key, value) -> append(key, value) }
                    if (requestMethod != HttpMethod.Get) append("Content-Type", "application/json")
                }
                queries.forEach { (key, value) -> parameter(key, value) }

                if (requestMethod != HttpMethod.Get) {
                    if (requestBody is ByteArray) setBody(ByteReadChannel(requestBody))
                    else if (requestBody != null) setBody(requestBody)
                    onUpload { bytesSentTotal, contentLength -> onUploading(bytesSentTotal, contentLength) }
                    onDownload { bytesReceivedTotal, contentLength -> onDownloading(bytesReceivedTotal, contentLength) }
                }
            }
            if (!listOf(
                    HttpStatusCode.OK,
                    HttpStatusCode.Created,
                    HttpStatusCode.NoContent,
                    HttpStatusCode.Accepted,
                    HttpStatusCode.Found,
                ).contains(response.status)
            ) {
                logRepository?.log("Network.${this.javaClass.name}", "Unexpected status code: ${response.status} (see log for details)")
                if (LOG_CONTENT_ON_ERROR) Log.w("Network.${this.javaClass.name}", "Unexpected status code: ${response.status} at $server$path\n${response.bodyAsText()}")
                else Log.w("Network.${this.javaClass.name}", "Unexpected status code: ${response.status} at $server$path")
            }
            return DataResponse(response.bodyAsText(), response.status)
        } catch (e: Exception) {
            logRepository?.log(
                "Network",
                "error when requesting $server$path (${e.javaClass.name}):\n${e.localizedMessage}"
            )
            e.printStackTrace()
            return when (e) {
                is ConnectTimeoutException, is HttpRequestTimeoutException -> DataResponse(
                    null,
                    null
                )

                is ConnectException, is UnknownHostException -> DataResponse(null, null)
                else -> DataResponse(null, null)
            }
        }
    }

    override suspend fun doRequestRaw(
        path: String,
        requestMethod: HttpMethod,
        requestBody: Any?,
        queries: Map<String, String>,
        onUploading: (bytesSentTotal: Long, contentLength: Long) -> Unit,
        onDownloading: (bytesReceivedTotal: Long, contentLength: Long) -> Unit
    ): DataResponse<ByteArray?> {
        try {
            logRepository?.log("Network", "Requesting ${requestMethod.value} $server$path")
            val response = client.request("$server$path") request@{
                method = requestMethod
                headers headers@{
                    if (authentication != null) {
                        val (key, value) = authentication!!.toHeader()
                        append(key, value)
                    }
                    globalHeaders.forEach { (key, value) -> append(key, value) }
                    if (requestMethod != HttpMethod.Get) append("Content-Type", "application/json")
                }
                queries.forEach { (key, value) -> parameter(key, value) }

                if (requestMethod != HttpMethod.Get) {
                    if (requestBody is ByteArray) setBody(ByteReadChannel(requestBody))
                    else if (requestBody != null) setBody(requestBody)
                    onUpload { bytesSentTotal, contentLength -> onUploading(bytesSentTotal, contentLength) }
                    onDownload { bytesReceivedTotal, contentLength -> onDownloading(bytesReceivedTotal, contentLength) }
                }
            }
            if (!listOf(
                    HttpStatusCode.OK,
                    HttpStatusCode.Created,
                    HttpStatusCode.NoContent,
                    HttpStatusCode.Accepted,
                    HttpStatusCode.Found,
                ).contains(response.status)
            ) {
                logRepository?.log("Network.${this.javaClass.name}", "Unexpected status code: ${response.status} (see log for details)")
                if (LOG_CONTENT_ON_ERROR) Log.w("Network.${this.javaClass.name}", "Unexpected status code: ${response.status} at $server$path\n${response.bodyAsText()}")
                else Log.w("Network.${this.javaClass.name}", "Unexpected status code: ${response.status} at $server$path")
            }
            return DataResponse(response.bodyAsChannel().toByteArray(), response.status)
        } catch (e: Exception) {
            logRepository?.log(
                "Network",
                "error when requesting $server$path (${e.javaClass.name}):\n${e.localizedMessage}"
            )
            e.printStackTrace()
            return when (e) {
                is ConnectTimeoutException, is HttpRequestTimeoutException -> DataResponse(
                    null,
                    null
                )

                is ConnectException, is UnknownHostException -> DataResponse(null, null)
                else -> DataResponse(null, null)
            }
        }
    }

    override suspend fun doRequestForm(
        path: String,
        requestMethod: HttpMethod,
        form: Map<String, String>,
        queries: Map<String, String>,
        onUploading: (bytesSentTotal: Long, contentLength: Long) -> Unit,
        onDownloading: (bytesReceivedTotal: Long, contentLength: Long) -> Unit
    ): DataResponse<String?> {
        try {
            logRepository?.log("Network", "Requesting ${requestMethod.value} $server$path")
            val response = client.submitForm(
                url = "$server$path",
                formParameters = parameters {
                    form.forEach { (key, value) -> append(key, value) }
                }
            ) request@{
                method = requestMethod
                headers headers@{
                    if (authentication != null) {
                        val (key, value) = authentication!!.toHeader()
                        append(key, value)
                    }
                    globalHeaders.forEach { (key, value) -> append(key, value) }
                    if (requestMethod != HttpMethod.Get) append("Content-Type", "application/json")
                }
                queries.forEach { (key, value) -> parameter(key, value) }

                onUpload { bytesSentTotal, contentLength -> onUploading(bytesSentTotal, contentLength) }
                onDownload { bytesReceivedTotal, contentLength -> onDownloading(bytesReceivedTotal, contentLength) }
            }
            if (!listOf(
                    HttpStatusCode.OK,
                    HttpStatusCode.Created,
                    HttpStatusCode.NoContent,
                    HttpStatusCode.Accepted,
                    HttpStatusCode.Found,
                ).contains(response.status)
            ) {
                logRepository?.log("Network.${this.javaClass.name}", "Unexpected status code: ${response.status} (see log for details)")
                if (LOG_CONTENT_ON_ERROR) Log.w("Network.${this.javaClass.name}", "Unexpected status code: ${response.status} at $server$path\n${response.bodyAsText()}")
                else Log.w("Network.${this.javaClass.name}", "Unexpected status code: ${response.status} at $server$path")
            }
            return DataResponse(response.bodyAsText(), response.status)
        } catch (e: Exception) {
            logRepository?.log(
                "Network",
                "error when requesting $server$path (${e.javaClass.name}):\n${e.localizedMessage}"
            )
            return when (e) {
                is ConnectTimeoutException, is HttpRequestTimeoutException -> DataResponse(
                    null,
                    null
                )

                is ConnectException, is UnknownHostException -> DataResponse(null, null)
                else -> DataResponse(null, null)
            }
        }
    }
}

interface Authentication {
    fun toHeader(): Pair<String, String>
}

class BasicAuthentication(
    private val username: String,
    private val password: String
) : Authentication {
    override fun toHeader(): Pair<String, String> {
        val credentials = "$username:$password"
        val base64Credentials =
            java.util.Base64.getEncoder().encodeToString(credentials.toByteArray())
        return "Authorization" to "Basic $base64Credentials"
    }
}

class BearerAuthentication(
    private val token: String
) : Authentication {
    override fun toHeader(): Pair<String, String> = "Authorization" to "Bearer $token"
}

class BsNetworkRepository(
    userAgent: String = DEFAULT_USER_AGENT,
    logRepository: LogRecordRepository?
) : NetworkRepositoryImpl(
    server = "https://beste.schule",
    userAgent = userAgent,
    logRepository = logRepository,
    init = {
        globalHeaders["Accept"] = "application/json"
    }
)

class NewsNetworkRepository(
    userAgent: String = DEFAULT_USER_AGENT,
    logRepository: LogRecordRepository?,
    keyValueRepository: KeyValueRepository
) : NetworkRepositoryImpl(
    server = keyValueRepository.getOnMainThread(Keys.VPPID_SERVER) ?: servers.first().apiHost,
    userAgent = userAgent,
    logRepository = logRepository
)

class Sp24NetworkRepository(
    userAgent: String = DEFAULT_USER_AGENT,
    logRepository: LogRecordRepository?
) : NetworkRepositoryImpl(
    server = "https://www.stundenplan24.de",
    userAgent = userAgent,
    logRepository = logRepository
)

class VppIdNetworkRepository(
    userAgent: String = DEFAULT_USER_AGENT,
    logRepository: LogRecordRepository?,
    keyValueRepository: KeyValueRepository
) : NetworkRepositoryImpl(
    server = keyValueRepository.getOnMainThread(Keys.VPPID_SERVER) ?: servers.first().apiHost,
    userAgent = userAgent,
    logRepository = logRepository
)

data class Response<C, V>(
    val code: C,
    val value: V
)