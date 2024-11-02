package es.jvbabi.vplanplus.feature.main_grades.view.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbGrade
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbInterval
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbSubject
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbTeacher
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbYear
import es.jvbabi.vplanplus.feature.main_grades.view.data.source.database.GradeDao
import es.jvbabi.vplanplus.feature.main_grades.view.data.source.database.SubjectDao
import es.jvbabi.vplanplus.feature.main_grades.view.data.source.database.TeacherDao
import es.jvbabi.vplanplus.feature.main_grades.view.data.source.database.YearDao
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.DownloadedGrade
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.GradeModifier
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Interval
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Subject
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Teacher
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Year
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.SchulverwalterResponse
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.BsNetworkRepository
import es.jvbabi.vplanplus.shared.data.Response
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GradeRepositoryImpl(
    private val teacherDao: TeacherDao,
    private val subjectDao: SubjectDao,
    private val gradeDao: GradeDao,
    private val yearDao: YearDao,
    private val bsNetworkRepository: BsNetworkRepository
) : GradeRepository {
    override fun getAllGrades(): Flow<List<Grade>> = flow {
        gradeDao.getAllGrades().collect {
            emit(it.map { g -> g.toModel() })
        }
    }

    override fun getGradesByUser(vppId: VppId, givenAt: LocalDate?): Flow<List<Grade>> {
        if (givenAt == null) return gradeDao.getGradesByUser(vppId.id).map { it.map { grade -> grade.toModel() } }
        return gradeDao.getGradesByUserAndGivenAt(vppId.id, givenAt).map { it.map { grade -> grade.toModel() } }
    }

    override suspend fun dropAll() {
        gradeDao.dropAll()
    }

    override suspend fun downloadYears(vppId: VppId.ActiveVppId): Map<Year, List<Interval>> {
        bsNetworkRepository.authentication = BearerAuthentication(vppId.schulverwalterToken ?: return emptyMap())
        val years = bsNetworkRepository.doRequest("/api/years").let {
            if (it.response == HttpStatusCode.Unauthorized) throw BsUnauthorizedException()
            if (it.response != HttpStatusCode.OK) throw BsRequestFailedException(it.response)
            Gson().fromJson(it.data, BsSchoolYearResponse::class.java).years
        }.associate { year ->
            Year(
                id = year.id,
                name = year.name,
                from = year.start,
                to = year.end,
            ) to year.intervals.map { interval ->
                Interval(
                    id = interval.id,
                    name = interval.name,
                    type = interval.type,
                    from = interval.start,
                    to = interval.end,
                    includedIntervalId = interval.includedIntervalId,
                    yearId = interval.id
                )
            }
        }
        return years
    }

    override suspend fun upsertYear(year: Year) {
        yearDao.upsert(
            DbYear(
                id = year.id,
                name = year.name,
                from = year.from,
                to = year.to
            )
        )
    }

    override suspend fun upsertInterval(year: Year, interval: Interval) {
        yearDao.upsertInterval(
            DbInterval(
                id = interval.id,
                name = interval.name,
                type = interval.type,
                from = interval.from,
                to = interval.to,
                includedIntervalId = interval.includedIntervalId,
                yearId = year.id
            )
        )
    }

    override suspend fun downloadGrades(activeVppId: VppId.ActiveVppId): Response<SchulverwalterResponse, List<DownloadedGrade>?> {
        val token = activeVppId.schulverwalterToken ?: return Response(SchulverwalterResponse.UNAUTHORIZED, null)
        bsNetworkRepository.authentication = BearerAuthentication(token)
        val result = bsNetworkRepository.doRequest("/api/grades?include=collection")
        if (result.response == HttpStatusCode.Unauthorized) return Response(SchulverwalterResponse.UNAUTHORIZED, null)
        if (result.response != HttpStatusCode.OK) return Response(SchulverwalterResponse.OTHER, null)
        return Response(SchulverwalterResponse.SUCCESS, Gson().fromJson(result.data, BsGradesResponse::class.java).data.map {
            val (gradeNumber, gradeModifier) = explodeGrade(it.value)
            DownloadedGrade(
                id = it.id,
                value = gradeNumber.toFloat(),
                modifier = gradeModifier,
                givenAt = LocalDate.parse(it.givenAt),
                subject = Subject(it.subject.id, it.subject.short, it.subject.name),
                givenBy = Teacher(it.teacher.id, it.teacher.firstname, it.teacher.short, it.teacher.lastname),
                vppId = activeVppId,
                type = it.collection.type,
                intervalId = it.collection.intervalId.toInt(),
                comment = it.collection.name
            )
        })
    }

    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.insert(DbSubject(subject.id, subject.short, subject.name))
    }

    override suspend fun upsertTeacher(teacher: Teacher) {
        teacherDao.insert(DbTeacher(teacher.id, teacher.short, teacher.firstname, teacher.lastname))
    }

    /**
     * Explodes a grade string into a number and a [GradeModifier]
     * @param input the input string to explode
     * @return a pair of the number and the [GradeModifier]
     * @see GradeModifier
     * @author Julius Babies
     */
    private fun explodeGrade(input: String): Pair<Int, GradeModifier> {
        val regex = "(\\d+)([-+]?)".toRegex()
        val matchResult = regex.find(input)
        val (number, modifier) = matchResult?.destructured ?: return Pair(0, GradeModifier.NEUTRAL)
        return Pair(number.toInt(), when (modifier) {
            "+" -> GradeModifier.PLUS
            "-" -> GradeModifier.MINUS
            else -> GradeModifier.NEUTRAL
        })
    }

    override suspend fun upsertGrade(grade: Grade) {
        upsertYear(grade.year)
        upsertInterval(grade.year, grade.interval)
        upsertSubject(grade.subject)
        upsertTeacher(grade.givenBy)
        gradeDao.upsert(
            DbGrade(
                id = grade.id,
                vppId = grade.vppId.id,
                value = grade.value,
                modifier = grade.modifier,
                subject = grade.subject.id,
                teacherId = grade.givenBy.id,
                givenAt = grade.givenAt,
                type = grade.type,
                comment = grade.comment,
                interval = grade.interval.id
            )
        )
    }

    override suspend fun deleteGrade(grade: Grade) {
        gradeDao.deleteById(grade.id)
    }
}

private data class BsGradesResponse(
    @SerializedName("data") val data: List<DownloadedSchulverwalterGrade>
)



private data class BsSchoolYearResponse(
    @SerializedName("data") val years: List<BsSchoolYear>
)

private data class BsSchoolYear(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("from") private val startRaw: String,
    @SerializedName("to") private val endRaw: String,
    @SerializedName("intervals") val intervals: List<BsSchoolYearInterval>
) {
    val start: LocalDate
        get() = LocalDate.parse(startRaw)

    val end: LocalDate
        get() = LocalDate.parse(endRaw)
}

private data class BsSchoolYearInterval(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("from") private val startRaw: String,
    @SerializedName("to") private val endRaw: String,
    @SerializedName("included_interval_id") val includedIntervalId: Long? = null
) {
    val start: LocalDate
        get() = LocalDate.parse(startRaw)

    val end: LocalDate
        get() = LocalDate.parse(endRaw)
}

private data class DownloadedSchulverwalterGrade(
    @SerializedName("id") val id: Long,
    @SerializedName("value") val value: String,
    @SerializedName("given_at") val givenAt: String,
    @SerializedName("subject") val subject: DownloadedSchulverwalterSubject,
    @SerializedName("teacher") val teacher: DownloadedSchulverwalterTeacher,
    @SerializedName("collection") val collection: DownloadedSchulverwalterCollection
)

private data class DownloadedSchulverwalterSubject(
    @SerializedName("id") val id: Long,
    @SerializedName("local_id") val short: String,
    @SerializedName("name") val name: String,
)

private data class DownloadedSchulverwalterTeacher(
    @SerializedName("id") val id: Long,
    @SerializedName("local_id") val short: String,
    @SerializedName("forename") val firstname: String,
    @SerializedName("name") val lastname: String
)

private data class DownloadedSchulverwalterCollection(
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("interval_id") val intervalId: Long
)

class BsUnauthorizedException : Exception("The token is invalid")
private class BsRequestFailedException(response: HttpStatusCode?) : Exception("The request failed with $response")