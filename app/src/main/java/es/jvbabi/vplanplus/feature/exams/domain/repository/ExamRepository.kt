package es.jvbabi.vplanplus.feature.exams.domain.repository

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.VppId
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZonedDateTime

interface ExamRepository {

    /**
     * Used to store an exam in the local database regardless whether it originates from the cloud or not
     * @param id if null, will create a negative one to represent local creation
     * @param author if null, will be treated as a local exam
     * @return the saved exam
     */
    suspend fun upsertExamLocally(
        id: Int? = null,
        subject: DefaultLesson,
        date: LocalDate,
        type: ExamType,
        topic: String,
        details: String?,
        group: Group,
        author: VppId?,
        createdAt: ZonedDateTime = ZonedDateTime.now()
    ): Flow<Exam>
}