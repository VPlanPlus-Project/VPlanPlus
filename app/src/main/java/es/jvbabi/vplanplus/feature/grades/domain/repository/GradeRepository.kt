package es.jvbabi.vplanplus.feature.grades.domain.repository

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.grades.domain.model.Grade
import kotlinx.coroutines.flow.Flow

interface GradeRepository {

    suspend fun updateGrades()

    fun getAllGrades(): Flow<List<Grade>>
    fun getGradesByUser(vppId: VppId): Flow<List<Grade>>
    suspend fun dropAll()
}