package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.Response
import es.jvbabi.vplanplus.domain.ResponseFactory
import es.jvbabi.vplanplus.shared.domain.repository.NetworkRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import java.net.ConnectException
import java.net.UnknownHostException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

const val DEFAULT_USER_AGENT = "VPlanPlus"

open class NetworkRepositoryImpl(
    server: String,
    private val userAgent: String = DEFAULT_USER_AGENT,
): NetworkRepository {

    private var server: String
    override var authentication: Authentication? = null

    init {
        this.server = server
        if (server.endsWith("/")) this.server = server.dropLast(1)
    }

    private val client = HttpClient(Android) {
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 5000
        }
        install(UserAgent) {
            agent = userAgent
        }
    }

    override suspend fun doRequest(
        path: String,
        requestMethod: HttpMethod,
        requestBody: String?
    ): DataResponse<String?> {
        try {
            val response = client.request("$server$path") request@{
                method = requestMethod
                headers headers@{
                    if (authentication != null) {
                        val (key, value) = authentication!!.toHeader()
                        append(key, value)
                    }
                }
                if (requestMethod != HttpMethod.Get) setBody(requestBody)
            }
            return DataResponse(response.bodyAsText(), ResponseFactory.getResponse(response.status.value))
        } catch (e: Exception) {
            return when (e) {
                is ConnectTimeoutException, is HttpRequestTimeoutException -> DataResponse(null, Response.NO_INTERNET)
                is ConnectException, is UnknownHostException -> DataResponse(null, Response.NO_INTERNET)
                else -> DataResponse(null, Response.OTHER)
            }
        }
    }
}

interface Authentication {
    fun toHeader(): Pair<String, String>
}

@ExperimentalEncodingApi
class BasicAuthentication(
    private val username: String,
    private val password: String
) : Authentication {
    override fun toHeader(): Pair<String, String> {
        val credentials = "$username:$password"
        val base64Credentials = Base64.encode(credentials.toByteArray())
        return "Authorization" to "Basic $base64Credentials"
    }
}

class TokenAuthentication(
    private val prefix: String,
    private val token: String
) : Authentication {
    override fun toHeader(): Pair<String, String> {
        return "Authorization" to "$prefix$token"
    }
}

class NewsNetworkRepository(
    userAgent: String = DEFAULT_USER_AGENT
): NetworkRepositoryImpl(
    server = "https://database-00.jvbabi.es",
    userAgent = userAgent
)

class Sp24NetworkRepository(
    userAgent: String = DEFAULT_USER_AGENT
): NetworkRepositoryImpl(
    server = "https://www.stundenplan24.de",
    userAgent = userAgent
)