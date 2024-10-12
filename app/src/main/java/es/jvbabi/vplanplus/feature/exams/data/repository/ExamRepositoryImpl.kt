package es.jvbabi.vplanplus.feature.exams.data.repository

import es.jvbabi.vplanplus.data.model.exam.DbExam
import es.jvbabi.vplanplus.data.source.database.dao.ExamDao
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZonedDateTime

class ExamRepositoryImpl(
    private val examDao: ExamDao
) : ExamRepository {
    override suspend fun upsertExamLocally(
        id: Int?,
        subject: DefaultLesson,
        date: LocalDate,
        type: ExamType,
        topic: String,
        details: String?,
        group: Group,
        author: VppId?,
        createdAt: ZonedDateTime
    ): Flow<Exam> {
        val examId = id ?: examDao.getCurrentLocalExamId().minus(1)

        examDao.saveExam(
            DbExam(
                id = examId,
                subject = subject.vpId,
                date = date,
                type = type.name,
                title = topic,
                description = details,
                createdAt = createdAt,
                createdBy = author?.id,
                groupId = group.groupId
            )
        )
        return examDao.getExam(examId).map { it.toModel() }
    }

    override fun getExams(date: LocalDate?, group: Group?): Flow<List<Exam>> =
        examDao.getExams(date, group?.groupId)
            .map { exams -> exams.map { it.toModel() } }
}