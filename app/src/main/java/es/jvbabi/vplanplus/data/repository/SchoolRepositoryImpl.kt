package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.SchoolDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.Flow

class SchoolRepositoryImpl(
    private val schoolDao: SchoolDao
) : SchoolRepository {
    override fun getSchools(): Flow<List<School>> {
        return schoolDao.getAll()
    }

    override suspend fun checkSchoolId(schoolId: String): SchoolIdCheckResult {
        val response: HttpResponse =
            HttpClient().request("https://www.stundenplan24.de/$schoolId") {
                method = HttpMethod.Get
            }
        return if (response.status.value == 403) SchoolIdCheckResult.VALID else SchoolIdCheckResult.NOT_FOUND
    }
}