package es.jvbabi.vplanplus.feature.exams.domain.repository

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.domain.model.VppId
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZonedDateTime

interface ExamRepository {

    /**
     * Used to store an exam in the local database regardless whether it originates from the cloud or not
     * @param id if null, will create a negative one to represent local creation
     * @param profile the profile of the creator, if the id is larger than zero, the vpp.ID will be used.
     * @param remindDaysBefore if null, the default reminder days will be used
     * @return the saved exam
     */
    suspend fun upsertExamLocally(
        id: Int? = null,
        subject: DefaultLesson,
        date: LocalDate,
        type: ExamCategory,
        topic: String,
        details: String?,
        profile: ClassProfile,
        createdAt: ZonedDateTime = ZonedDateTime.now(),
        remindDaysBefore: Set<Int>? = null,
        createdBy: VppId?,
        isPublic: Boolean = false
    ): Flow<Exam>

    suspend fun insertExamCloud(
        subject: DefaultLesson,
        date: LocalDate,
        type: ExamCategory,
        topic: String,
        details: String?,
        profile: ClassProfile,
        createdAt: ZonedDateTime = ZonedDateTime.now(),
        isPublic: Boolean,
    ): Result<Int>

    suspend fun updateExam(
        exam: Exam,
        profile: ClassProfile
    ): Result<Boolean>

    fun getExams(
        date: LocalDate? = null,
        profile: ClassProfile?
    ): Flow<List<Exam>>

    fun getExamById(
        id: Int,
        profile: ClassProfile? = null
    ): Flow<Exam?>

    suspend fun deleteExamById(examId: Int, profile: ClassProfile?, onlyLocal: Boolean = false)
}