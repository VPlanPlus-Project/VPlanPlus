package es.jvbabi.vplanplus.feature.main_grades.domain.repository

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import kotlinx.coroutines.flow.Flow

interface GradeRepository {

    suspend fun updateGrades(): List<Grade>

    fun getAllGrades(): Flow<List<Grade>>
    fun getGradesByUser(vppId: VppId): Flow<List<Grade>>
    suspend fun dropAll()
}