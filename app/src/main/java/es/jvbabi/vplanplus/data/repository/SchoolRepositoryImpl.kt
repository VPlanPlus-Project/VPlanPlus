package es.jvbabi.vplanplus.data.repository

import android.util.Log
import es.jvbabi.vplanplus.data.source.SchoolDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.basicAuth
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.Flow
import java.net.UnknownHostException

class SchoolRepositoryImpl(
    private val schoolDao: SchoolDao
) : SchoolRepository {
    override fun getSchools(): Flow<List<School>> {
        return schoolDao.getAll()
    }

    override suspend fun checkSchoolId(schoolId: String): SchoolIdCheckResult? {
        return try {
            val response: HttpResponse =
                HttpClient().request("https://www.stundenplan24.de/$schoolId") {
                    method = HttpMethod.Get
                }
            if (response.status.value == 403) SchoolIdCheckResult.VALID else SchoolIdCheckResult.NOT_FOUND
        } catch (e: UnknownHostException) {
            Log.d("SchoolRepositoryImpl", "offline")
            null
        }
    }

    override suspend fun login(
        schoolId: String,
        username: String,
        password: String
    ): Response {
        return try {
            val response: HttpResponse =
                HttpClient {
                    install(HttpTimeout) {
                        requestTimeoutMillis = 5000
                        connectTimeoutMillis = 5000
                        socketTimeoutMillis = 5000
                    }
                }.request("https://www.stundenplan24.de/$schoolId/wplan") {
                    method = HttpMethod.Get
                    basicAuth(username, password)
                }
            Log.d("SchoolRepositoryImpl", "called https://www.stundenplan24.de/$schoolId/wplan; status: ${response.status.value}")
            when (response.status.value) {
                200 -> Response.SUCCESS
                401 -> Response.WRONG_CREDENTIALS
                else -> Response.OTHER
            }
        }catch (e: Exception) {
            when (e) {
                is UnknownHostException -> return Response.NO_INTERNET
                is ConnectTimeoutException -> return Response.NO_INTERNET
                is HttpRequestTimeoutException -> return Response.NO_INTERNET
                else -> {
                    Log.d("SchoolRepositoryImpl", "other error: ${e.javaClass.name} ${e.message}")
                    return Response.OTHER
                }
            }
        }
    }

    override suspend fun createSchool(schoolId: String, username: String, password: String) {
        schoolDao.insert(
            School(
                id = schoolId,
                username = username,
                password = password,
                name = "Schule $schoolId"
            )
        )
    }
}