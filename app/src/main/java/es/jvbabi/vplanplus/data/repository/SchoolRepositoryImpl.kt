package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.SchoolDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import kotlinx.coroutines.flow.Flow

class SchoolRepositoryImpl(
    private val schoolDao: SchoolDao
) : SchoolRepository {
    override fun getSchools(): Flow<List<School>> {
        return schoolDao.getAll()
    }
}