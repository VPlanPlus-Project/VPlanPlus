package es.jvbabi.vplanplus.data.repository

import android.util.Log
import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.usecase.Response
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.basicAuth
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import java.net.UnknownHostException

class BaseDataRepositoryImpl(

): BaseDataRepository {
    override suspend fun getBaseData(
        schoolId: String,
        username: String,
        password: String
    ): OnlineResponse<BaseDataParserStudents?> {
        return try {
            val response = HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                    socketTimeoutMillis = 5000
                }
            }.request("https://www.stundenplan24.de/$schoolId/wplan/wdatenk/SPlanKl_Basis.xml") {
                method = HttpMethod.Get
                basicAuth(username, password)
            }
            OnlineResponse(BaseDataParserStudents(response.bodyAsText()), Response.SUCCESS)
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException -> return OnlineResponse(null, Response.NO_INTERNET)
                else -> {
                    Log.d("HolidayRepositoryImpl", "other error: ${e.javaClass.name} ${e.message}")
                    return OnlineResponse(null, Response.OTHER)
                }
            }
        }
    }
}