package es.jvbabi.vplanplus.data.repository

import android.util.Log
import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.usecase.Response
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.basicAuth
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod.Companion.Get
import java.net.UnknownHostException
import java.time.LocalDate

class VPlanRepositoryImpl : VPlanRepository {
    override suspend fun getVPlanData(school: School, date: LocalDate): OnlineResponse<VPlanData?> {
        return try {
            val response = HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                    socketTimeoutMillis = 5000
                }
            }.request("https://www.stundenplan24.de/${school.id}/wplan/wdatenk/WPlanKl_${date.year}${date.monthValue}${date.dayOfMonth}.xml") {
                method = Get
                basicAuth(school.username, school.password)
            }
            OnlineResponse(VPlanData(response.bodyAsText(), school.id), Response.SUCCESS)
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException -> OnlineResponse(null, Response.NO_INTERNET)
                else -> {
                    Log.d("HolidayRepositoryImpl", "other error: ${e.javaClass.name} ${e.message}")
                    OnlineResponse(null, Response.OTHER)
                }
            }
        }
    }
}