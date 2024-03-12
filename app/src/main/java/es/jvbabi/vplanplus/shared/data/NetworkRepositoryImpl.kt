package es.jvbabi.vplanplus.shared.data

import android.util.Log
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.shared.domain.repository.NetworkRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
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
        requestBody: String?,
        queries: Map<String, String>
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

                if (requestMethod != HttpMethod.Get) setBody(requestBody ?: "{}")
            }
            if (!listOf(
                    HttpStatusCode.OK,
                    HttpStatusCode.Created,
                    HttpStatusCode.NoContent
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

@Deprecated("Use BearerAuthentication instead", replaceWith = ReplaceWith("BearerAuthentication(token)"))
class TokenAuthentication(
    private val prefix: String,
    private val token: String
) : Authentication {
    override fun toHeader(): Pair<String, String> {
        return "Authorization" to "$prefix$token"
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
    server = keyValueRepository.getOnMainThread(Keys.VPPID_SERVER) ?: Keys.VPPID_SERVER_DEFAULT,
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
    server = keyValueRepository.getOnMainThread(Keys.VPPID_SERVER) ?: Keys.VPPID_SERVER_DEFAULT,
    userAgent = userAgent,
    logRepository = logRepository
)