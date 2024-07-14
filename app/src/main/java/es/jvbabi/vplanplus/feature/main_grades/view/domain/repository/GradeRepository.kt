package es.jvbabi.vplanplus.feature.main_grades.view.domain.repository

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.DownloadedGrade
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Interval
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Subject
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Teacher
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Year
import es.jvbabi.vplanplus.shared.data.Response
import kotlinx.coroutines.flow.Flow

interface GradeRepository {

    @Deprecated("Use usecase instead")
    suspend fun updateGrades(): List<Grade>

    fun getAllGrades(): Flow<List<Grade>>
    fun getGradesByUser(vppId: VppId): Flow<List<Grade>>
    suspend fun dropAll()

    suspend fun downloadGrades(activeVppId: VppId.ActiveVppId): Response<SchulverwalterResponse, List<DownloadedGrade>?>
    suspend fun upsertGrade(grade: Grade)
    suspend fun deleteGrade(grade: Grade)

    suspend fun upsertSubject(subject: Subject)
    suspend fun upsertTeacher(teacher: Teacher)

    suspend fun downloadYears(vppId: VppId.ActiveVppId): Map<Year, List<Interval>>
    suspend fun upsertYear(year: Year)
    suspend fun upsertInterval(year: Year, interval: Interval)
}

enum class SchulverwalterResponse {
    SUCCESS,
    UNAUTHORIZED,
    NO_INTERNET,
    OTHER
}