package es.jvbabi.vplanplus.feature.exams.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import es.jvbabi.vplanplus.data.model.exam.DbExam
import es.jvbabi.vplanplus.data.repository.ResponseDataWrapper
import es.jvbabi.vplanplus.data.source.database.dao.ExamDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
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
        remindDaysBefore: Set<Int>?,
        createdBy: VppId?,
        isPublic: Boolean
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
                createdBy = if (examId < 0) null else createdBy?.id,
                groupId = profile.group.groupId,
                useDefaultNotifications = remindDaysBefore == null,
                isPublic = isPublic,
            )
        )
        remindDaysBefore?.forEach { daysBefore ->
            examDao.insertExamReminder(examId, profile.id, daysBefore)
        }
        return examDao.getExam(examId).map { it!!.toModel(profile) }
    }

    override suspend fun insertExamCloud(
        subject: DefaultLesson,
        date: LocalDate,
        type: ExamCategory,
        topic: String,
        details: String?,
        profile: ClassProfile,
        createdAt: ZonedDateTime,
        isPublic: Boolean
    ): Result<Int> {
        assert(profile.vppId != null) { "No VPP-ID found for profile" }
        vppIdNetworkRepository.authentication = BearerAuthentication(profile.vppId!!.vppIdToken)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/entity/assessment",
            requestMethod = HttpMethod.Post,
            requestBody = Gson().toJson(
                NewExamRequest(
                    date = createdAt.toEpochSecond(),
                    subject = "sp24.${profile.getSchool().sp24SchoolId}.${subject.vpId}",
                    title = topic,
                    description = details,
                    isPublic = isPublic,
                    groupId = profile.group.groupId,
                    type = type.code,
                )
            )
        )
        if (response.response != HttpStatusCode.Created) return Result.failure(Exception("Error creating exam"))
        return Result.success(ResponseDataWrapper.fromJson<NewExamResponse>(response.data)!!.id)
    }

    override suspend fun updateExam(exam: Exam, profile: ClassProfile): Result<Boolean> {
        val oldExam = getExamById(exam.id, profile).first() ?: return Result.success(false)
        if (exam is Exam.Cloud && !exam.equalsContent(oldExam)) {
            // Update exam in cloud
            vppIdNetworkRepository.authentication = BearerAuthentication(profile.vppId!!.vppIdToken)
            val response = vppIdNetworkRepository.doRequest(
                path = "/api/$API_VERSION/entity/assessment/${exam.id}",
                requestMethod = HttpMethod.Patch,
                requestBody = GsonBuilder()
                    .registerTypeAdapter(Pair::class.java, PatchExamDiffAdapter())
                    .create()
                    .toJson(oldExam to exam),
            )
            if (response.response != HttpStatusCode.OK) return Result.failure(Exception("Error updating exam"))
        }
        examDao.saveExam(
            DbExam(
                id = exam.id,
                title = exam.title,
                description = exam.description,
                isPublic = (exam as? Exam.Cloud)?.isPublic ?: false,
                groupId = profile.group.groupId,
                type = exam.type.code,
                date = exam.date,
                createdAt = exam.createdAt,
                subject = exam.subject?.vpId,
                createdBy = (exam as? Exam.Cloud)?.createdBy?.id,
                useDefaultNotifications = false
            )
        )
        return Result.success(true)
    }

    override fun getExams(date: LocalDate?, profile: ClassProfile?): Flow<List<Exam>> =
        examDao.getExams(date, profile?.group?.groupId)
            .map { exams -> exams.map { it.toModel(profile) } }

    override fun getExamById(id: Int, profile: ClassProfile?): Flow<Exam?> {
        return examDao.getExam(id).map { it?.toModel(profile) }
    }

    override suspend fun downloadAssessments(profile: ClassProfile): Result<List<ExamsResponse>> {
        vppIdNetworkRepository.authentication = profile.vppId?.vppIdToken?.let { BearerAuthentication(it) }
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/entity/assessment",
            requestMethod = HttpMethod.Get,
            requestBody = null,
            queries = mapOf("filter_group" to profile.group.groupId.toString())
        )
        if (response.response != HttpStatusCode.OK) return Result.failure(Exception("Response ${response.response}: ${response.data}"))
        val data = ResponseDataWrapper.fromJson<List<ExamsResponse>>(response.data) ?: return Result.failure(Exception("No data or wrong data format: ${response.data}"))
        return Result.success(data)
    }

    override suspend fun deleteExamById(examId: Int, profile: ClassProfile?, onlyLocal: Boolean) {
        if (examId > 0 && !onlyLocal) {
            if (profile?.vppId?.vppIdToken == null) throw Exception("No VPP-ID found for profile")
            vppIdNetworkRepository.authentication = BearerAuthentication(profile.vppId.vppIdToken)
            vppIdNetworkRepository.doRequest(
                path = "/api/$API_VERSION/entity/assessment/${examId}",
                requestMethod = HttpMethod.Delete,
                requestBody = null,
            )
        }

        examDao.deleteExamById(examId)
    }

    override suspend fun clearCache() {
        examDao.clearCache()
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

data class ExamsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("subject") val subject: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("group") val groupId: Int,
    @SerializedName("is_public") val isPublic: Boolean,
    @SerializedName("date") val date: Int,
    @SerializedName("type") val type: String,
    @SerializedName("created_by") val createdBy: Int,
    @SerializedName("created_at") val createdAt: Int,
)

private class PatchExamDiffAdapter : TypeAdapter<Pair<Exam.Cloud, Exam.Cloud>>() {
    override fun write(out: JsonWriter, value: Pair<Exam.Cloud, Exam.Cloud>?) {
        val (before, after) = value ?: return
        assert(before.id == after.id)
        out.beginObject()
        if (before.title != after.title) out.name("title").value(after.title)
        if (before.description != after.description) out.name("description").value(after.description)
        if (before.type != after.type) out.name("type").value(after.type.code)
        if (before.isPublic != after.isPublic) out.name("is_public").value(after.isPublic)
        if (before.date != after.date) out.name("date").value(after.date.atStartOfDay(ZoneId.of("UTC")).toEpochSecond())
        out.endObject()
    }

    override fun read(reader: JsonReader): Pair<Exam.Cloud, Exam.Cloud>? {
        throw NotImplementedError("Not yet implemented")
    }
}