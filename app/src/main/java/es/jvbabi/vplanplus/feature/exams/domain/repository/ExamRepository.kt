package es.jvbabi.vplanplus.feature.exams.domain.repository

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZonedDateTime

interface ExamRepository {

    /**
     * Used to store an exam in the local database regardless whether it originates from the cloud or not
     * @param id if null, will create a negative one to represent local creation
     * @param profile the profile of the creator, if the id is larger than zero, its vpp.ID will be associated with the exam
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
        remindDaysBefore: Set<Int>? = null
    ): Flow<Exam>

    /**
     * @param id If null, will create a new exam. If not null, will update the exam with the given id.
     */
    suspend fun upsertExamCloud(
        id: Int? = null,
        subject: DefaultLesson,
        date: LocalDate,
        type: ExamCategory,
        topic: String,
        details: String?,
        profile: ClassProfile,
        createdAt: ZonedDateTime = ZonedDateTime.now(),
        isPublic: Boolean,
    ): Result<Int>

    fun getExams(
        date: LocalDate? = null,
        profile: ClassProfile?
    ): Flow<List<Exam>>

    fun getExamById(
        id: Int,
        profile: ClassProfile? = null
    ): Flow<Exam?>

    suspend fun updateExamLocally(
        exam: Exam,
        profile: ClassProfile
    )

    suspend fun deleteExamLocallyById(examId: Int)
}