package es.jvbabi.vplanplus.feature.exams.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.model.exam.DbExam
import es.jvbabi.vplanplus.data.repository.ResponseDataWrapper
import es.jvbabi.vplanplus.data.source.database.dao.ExamDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZonedDateTime

class ExamRepositoryImpl(
    private val examDao: ExamDao,
    private val vppIdNetworkRepository: VppIdNetworkRepository
) : ExamRepository {
    override suspend fun upsertExamLocally(
        id: Int?,
        subject: DefaultLesson,
        date: LocalDate,
        type: ExamCategory,
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
                groupId = profile.group.groupId,
                useDefaultNotifications = remindDaysBefore == null
            )
        )
        remindDaysBefore?.forEach { daysBefore ->
            examDao.insertExamReminder(examId, profile.id, daysBefore)
        }
        return examDao.getExam(examId).map { it!!.toModel(profile) }
    }

    override suspend fun upsertExamCloud(
        id: Int?,
        subject: DefaultLesson,
        date: LocalDate,
        type: ExamCategory,
        topic: String,
        details: String?,
        profile: ClassProfile,
        createdAt: ZonedDateTime,
        isPublic: Boolean
    ): Result<Int> {
        if (id != null) TODO("Not implemented yet")
        assert(profile.vppId != null) { "No VPP-ID found for profile" }
        vppIdNetworkRepository.authentication = BearerAuthentication(profile.vppId!!.vppIdToken)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/entity/assessment",
            requestMethod = HttpMethod.Post,
            requestBody = Gson().toJson(NewExamRequest(
                date = createdAt.toEpochSecond(),
                subject = "sp24.${profile.getSchool().sp24SchoolId}.${subject.vpId}",
                title = topic,
                description = details,
                isPublic = isPublic,
                groupId = profile.group.groupId,
                type = type.code,
            ))
        )
        if (response.response != HttpStatusCode.Created) return Result.failure(Exception("Error creating exam"))
        return Result.success(ResponseDataWrapper.fromJson<NewExamResponse>(response.data)!!.id)
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
            groupId = exam.group.groupId,
            useDefaultNotifications = exam.remindDaysBefore == exam.type.remindDaysBefore
        ))
        examDao.deleteExamReminders(exam.id)
        if (exam.remindDaysBefore != exam.type.remindDaysBefore) {
            exam.remindDaysBefore.forEach { daysBefore ->
                examDao.insertExamReminder(exam.id, profile.id, daysBefore)
            }
        }
    }

    override suspend fun deleteExamLocallyById(examId: Int) {
        examDao.deleteExamById(examId)
    }
}

private data class NewExamRequest(
    @SerializedName("date") val date: Long,
    @SerializedName("subject") val subject: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("is_public") val isPublic: Boolean,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("type") val type: String,
)

private data class NewExamResponse(
    @SerializedName("assessment_id") val id: Int
)