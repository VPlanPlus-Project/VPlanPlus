package es.jvbabi.vplanplus.shared.data

import android.util.Log
import es.jvbabi.vplanplus.data.source.database.dao.SchoolDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.feature.onboarding.domain.model.xml.ClassBaseData
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.basicAuth
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import java.net.UnknownHostException

class SchoolRepositoryImpl(
    private val sp24NetworkRepository: Sp24NetworkRepository,
    private val schoolDao: SchoolDao,
    private val firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
) : SchoolRepository {
    override suspend fun getSchools(): List<School> {
        return schoolDao.getAll()
    }

    override suspend fun deleteSchool(schoolId: Long) {
        schoolDao.delete(schoolId)
        firebaseCloudMessagingManagerRepository.updateToken(null)
    }

    override suspend fun checkSchoolId(schoolId: Long): SchoolIdCheckResult? {
        val result = sp24NetworkRepository.doRequest("/$schoolId")
        if (result.data == null || result.response == null) return null
        return when (result.response) {
            HttpStatusCode.Forbidden -> SchoolIdCheckResult.VALID
            HttpStatusCode.NotFound -> SchoolIdCheckResult.NOT_FOUND
            else -> null
        }
    }

    override suspend fun login(
        schoolId: Long,
        username: String,
        password: String
    ): HttpStatusCode? {
        return try {
            val response: HttpResponse =
                HttpClient {
                    install(HttpTimeout) {
                        requestTimeoutMillis = 10000
                        connectTimeoutMillis = 10000
                        socketTimeoutMillis = 10000
                    }
                }.request("https://www.stundenplan24.de/$schoolId/wplan") {
                    method = HttpMethod.Get
                    basicAuth(username, password)
                }
            Log.d("SchoolRepositoryImpl", "called https://www.stundenplan24.de/$schoolId/wplan; status: ${response.status.value}")
            response.status
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException -> return null
                is ConnectTimeoutException -> return null
                is HttpRequestTimeoutException -> return null
                else -> {
                    Log.d("SchoolRepositoryImpl", "other error: ${e.javaClass.name} ${e.message}")
                    return null
                }
            }
        }
    }

    override suspend fun createSchool(schoolId: Long, username: String, password: String, name: String, daysPerWeek: Int, fullyCompatible: Boolean) {
        schoolDao.insert(
            School(
                schoolId = schoolId,
                username = username,
                password = password,
                name = name,
                daysPerWeek = daysPerWeek,
                fullyCompatible = fullyCompatible
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
                    requestTimeoutMillis = 10000
                    connectTimeoutMillis = 10000
                    socketTimeoutMillis = 10000
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

    override suspend fun getSchoolFromId(schoolId: Long): School? {
        return schoolDao.getSchoolFromId(schoolId)
    }

    override suspend fun getSchoolByName(schoolName: String): School {
        return schoolDao.getSchoolByName(schoolName)
    }

    override suspend fun updateCredentialsValid(school: School, credentialsValid: Boolean?) {
        schoolDao.updateCredentialsValid(school.schoolId, credentialsValid)
    }

    override suspend fun updateCredentials(school: School, username: String, password: String) {
        schoolDao.updateCredentials(school.schoolId, username, password)
    }
}