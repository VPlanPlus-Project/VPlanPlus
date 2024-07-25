package es.jvbabi.vplanplus.shared.data

import android.util.Log
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.repository.ResponseDataWrapper
import es.jvbabi.vplanplus.data.source.database.dao.SchoolDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.ClassBaseData
import es.jvbabi.vplanplus.domain.repository.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.UpdateFcmTokenUseCase
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
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val schoolDao: SchoolDao,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) : SchoolRepository {
    override suspend fun getSchools(): List<School> {
        return schoolDao.getAll()
    }

    override suspend fun deleteSchool(schoolId: Int) {
        schoolDao.delete(schoolId)
        updateFcmTokenUseCase()
    }

    override suspend fun checkSchoolId(schoolId: Int): SchoolIdCheckResult? {
        val result = sp24NetworkRepository.doRequest("/$schoolId/")
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

    override suspend fun createSchool(
        schoolId: Int,
        sp24SchoolId: Int,
        username: String,
        password: String,
        name: String,
        daysPerWeek: Int,
        fullyCompatible: Boolean
    ) {
        schoolDao.insert(
            School(
                id = schoolId,
                sp24SchoolId = sp24SchoolId,
                username = username,
                password = password,
                name = name,
                daysPerWeek = daysPerWeek,
                fullyCompatible = fullyCompatible
            )
        )
    }

    override suspend fun getSchoolInfoBySp24DataOnline(sp24SchoolId: Int, username: String, password: String): SchoolInformation? {
        vppIdNetworkRepository.authentication = BasicAuthentication("$username@$sp24SchoolId", password)
        val getSchoolInformation = vppIdNetworkRepository.doRequest("/api/${API_VERSION}/school/sp24/get_school")
        if (getSchoolInformation.response != HttpStatusCode.OK) return null
        val schoolInformation = getSchoolInformation.data?.let {
            try {
                val schoolInformation = ResponseDataWrapper.fromJson<SchoolInformation>(getSchoolInformation.data)
                if (schoolInformation.sp24Id.toInt() != sp24SchoolId) return null
                schoolInformation
            } catch (e: Exception) {
                return null
            }
        } ?: return null
        return schoolInformation
    }

    override suspend fun updateSchoolName(schoolId: Int, name: String) {
        schoolDao.updateName(schoolId, name)
    }

    override suspend fun getSchoolNameOnline(
        schoolId: Int,
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

    override suspend fun getSchoolFromId(schoolId: Int): School? {
        return schoolDao.getSchoolFromId(schoolId)
    }

    override suspend fun getSchoolBySp24Id(sp24SchoolId: Int): School? {
        return schoolDao.getSchoolBySp24Id(sp24SchoolId)
    }

    override suspend fun getSchoolByName(schoolName: String): School {
        return schoolDao.getSchoolByName(schoolName)
    }

    override suspend fun updateCredentialsValid(school: School, credentialsValid: Boolean?) {
        schoolDao.updateCredentialsValid(school.id, credentialsValid)
    }

    override suspend fun updateCredentials(school: School, username: String, password: String) {
        schoolDao.updateCredentials(school.id, username, password)
    }
}

data class SchoolInformation(
    @SerializedName("school_id") val schoolId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("sp24_id") val sp24Id: Long,
)