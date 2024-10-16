package es.jvbabi.vplanplus.feature.exams.data.repository

import es.jvbabi.vplanplus.data.model.exam.DbExam
import es.jvbabi.vplanplus.data.source.database.dao.ExamDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamType
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
        profile: ClassProfile,
        createdAt: ZonedDateTime,
        remindDaysBefore: Set<Int>?
    ): Flow<Exam> {
        val examId = id ?: examDao.getCurrentLocalExamId().minus(1)

        examDao.saveExam(
            DbExam(
                id = examId,
                subject = subject.vpId,
                date = date,
                type = type.code,
                title = topic,
                description = details,
                createdAt = createdAt,
                createdBy = if (examId < 0) null else profile.vppId?.id,
                groupId = profile.group.groupId
            )
        )
        remindDaysBefore?.forEach { daysBefore ->
            if (LocalDate.now().until(date).days < daysBefore) return@forEach
            examDao.insertExamReminder(examId, profile.id, daysBefore, false)
        }
        return examDao.getExam(examId).map { it!!.toModel(profile) }
    }

    override fun getExams(date: LocalDate?, profile: ClassProfile?): Flow<List<Exam>> =
        examDao.getExams(date, profile?.group?.groupId)
            .map { exams -> exams.map { it.toModel(profile) } }

    override fun getExamById(id: Int, profile: ClassProfile?): Flow<Exam?> {
        return examDao.getExam(id).map { it?.toModel(profile) }
    }

    override suspend fun updateExamLocally(
        exam: Exam,
        profile: ClassProfile
    ) {
        examDao.saveExam(DbExam(
            id = exam.id,
            subject = exam.subject?.vpId,
            date = exam.date,
            type = exam.type.code,
            title = exam.title,
            description = exam.description,
            createdAt = exam.createdAt,
            createdBy = exam.createdBy?.id,
            groupId = exam.group.groupId
        ))
        examDao.deleteExamReminders(exam.id)
        if (exam.assessmentReminders != exam.type.remindDaysBefore) {
            exam.assessmentReminders.forEach { reminder ->
                if (LocalDate.now().until(exam.date).days < reminder.daysBefore) return@forEach
                examDao.insertExamReminder(exam.id, reminder.profile.id, reminder.daysBefore, reminder.hasDismissed)
            }
        }
    }

    override suspend fun deleteExamLocallyById(examId: Int) {
        examDao.deleteExamById(examId)
    }
}