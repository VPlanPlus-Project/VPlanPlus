package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.School
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    fun getSchools(): Flow<List<School>>
}