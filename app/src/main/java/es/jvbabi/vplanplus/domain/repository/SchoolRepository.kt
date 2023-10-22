package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    fun getSchools(): Flow<List<School>>

    suspend fun checkSchoolId(schoolId: String): SchoolIdCheckResult
}