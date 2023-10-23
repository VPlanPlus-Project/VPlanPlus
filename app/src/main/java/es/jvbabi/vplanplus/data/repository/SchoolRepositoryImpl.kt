package es.jvbabi.vplanplus.data.repository

import android.util.Log
import es.jvbabi.vplanplus.data.source.SchoolDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import es.jvbabi.vplanplus.util.ErrorType
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
    ): Flow<ErrorType> {
        val response: HttpResponse =
            HttpClient().request("https://www.stundenplan24.de/$schoolId") {
                method = HttpMethod.Get
            }
        return flowOf(when (response.status.value) {
            200 -> ErrorType.NONE
            403 -> ErrorType.UNAUTHORIZED
            else -> ErrorType.OTHER
        })
    }
}