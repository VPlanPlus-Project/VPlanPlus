package es.jvbabi.vplanplus.data.repository

import android.util.Log
import es.jvbabi.vplanplus.data.source.database.dao.SchoolDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.ClassBaseData
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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import java.net.UnknownHostException

class SchoolRepositoryImpl(
    private val schoolDao: SchoolDao
) : SchoolRepository {
    override suspend fun getSchools(): List<School> {
        return schoolDao.getAll()
    }

    override suspend fun checkSchoolId(schoolId: Long): SchoolIdCheckResult? {
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
        schoolId: Long,
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

    override suspend fun createSchool(schoolId: Long, username: String, password: String, name: String, daysPerWeek: Int) {
        schoolDao.insert(
            School(
                id = schoolId,
                username = username,
                password = password,
                name = name,
                daysPerWeek = daysPerWeek
            )
        )
    }

    override suspend fun updateSchoolName(schoolId: Long, name: String) {
        schoolDao.updateName(schoolId, name)
    }

    override suspend fun getSchoolNameOnline(
        schoolId: Long,
        username: String,
        password: String
    ): String {
        try {
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
            val baseData = ClassBaseData(response.bodyAsText())
            return baseData.schoolName
        } catch (e: Exception) {
            return ""
        }
    }

    override fun getSchoolFromId(schoolId: Long): School {
        return schoolDao.getSchoolFromId(schoolId)
    }

    override suspend fun getSchoolByName(schoolName: String): School {
        return schoolDao.getSchoolByName(schoolName)
    }
}