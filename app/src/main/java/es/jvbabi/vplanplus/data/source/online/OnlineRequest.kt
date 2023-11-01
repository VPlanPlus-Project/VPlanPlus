package es.jvbabi.vplanplus.data.source.online

import android.util.Log
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.usecase.Response
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.basicAuth
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import java.net.ConnectException
import java.net.UnknownHostException

object OnlineRequest {
    private suspend fun getRawResponse(url: String, username: String?, password: String?): HttpResponse {
        return HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 5000
            }
        }.request(url) {
            method = HttpMethod.Get
            if (username != null && password != null) basicAuth(username, password)
        }
    }

    /**
     * Requests the given url and returns the response body as String if the request was successful, null if the credentials were wrong or an error code if the request failed
     * @author Julius Babies
     * @param url the url to request
     * @param username the username to use for basic auth, null if no basic auth should be used
     * @param password the password to use for basic auth, null if no basic auth should be used
     * @return DataResponse<String?> with the response body as String if the request was successful, null if the credentials were wrong or an error code if the request failed
     */
    suspend fun getResponse(url: String, username: String?, password: String?): DataResponse<String?> {
        return try {
            val response = getRawResponse(url, username, password)
            when (response.status.value) {
                200 -> DataResponse(response.bodyAsText(), Response.SUCCESS)
                401 -> DataResponse(null, Response.WRONG_CREDENTIALS)
                else -> {
                    Log.d("OnlineRequest", "other error on $url: ${response.status.value}")
                    DataResponse(response.bodyAsText(), Response.OTHER)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException, is ConnectException -> return DataResponse(
                    null,
                    Response.NO_INTERNET
                )

                else -> {
                    Log.d("OnlineRequest", "other error on $url: ${e.stackTraceToString()}")
                    return DataResponse(null, Response.OTHER)
                }
            }
        }
    }
}