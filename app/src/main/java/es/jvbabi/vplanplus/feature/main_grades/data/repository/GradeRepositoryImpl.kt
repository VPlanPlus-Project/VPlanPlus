package es.jvbabi.vplanplus.feature.main_grades.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_GRADES
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbGrade
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbInterval
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbSubject
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbTeacher
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbYear
import es.jvbabi.vplanplus.feature.main_grades.data.source.database.GradeDao
import es.jvbabi.vplanplus.feature.main_grades.data.source.database.SubjectDao
import es.jvbabi.vplanplus.feature.main_grades.data.source.database.TeacherDao
import es.jvbabi.vplanplus.feature.main_grades.data.source.database.YearDao
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.domain.model.GradeModifier
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Subject
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Teacher
import es.jvbabi.vplanplus.feature.main_grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.BsNetworkRepository
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GradeRepositoryImpl(
    private val teacherDao: TeacherDao,
    private val subjectDao: SubjectDao,
    private val gradeDao: GradeDao,
    private val yearDao: YearDao,
    private val bsNetworkRepository: BsNetworkRepository,
    private val vppIdRepository: VppIdRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository,
    private val logRecordRepository: LogRecordRepository
) : GradeRepository {

    override suspend fun updateGrades(): List<Grade> {
        val vppIds = vppIdRepository.getActiveVppIds().first()
        val newGrades = mutableListOf<Grade>()

        vppIds.forEach vppId@{ vppId ->
            try {
                val bsToken = vppIdRepository.getBsToken(vppId) ?: return@vppId
                updateYears(bsToken)
                val responseGrades = getRawGrades(bsToken)

                val subjects = subjectDao.getSubjects().first().map { it.toModel() }.toMutableList()
                val teachers = teacherDao.getAllTeachers().first().map { it.toModel() }.toMutableList()
                val existingGrades = gradeDao.getGradesByUser(vppId.id).first().map { it.toModel() }.toMutableList()

                responseGrades.forEach { dataGrade ->
                    val existingGrade = existingGrades.firstOrNull { it.id == dataGrade.id }
                    if (!subjects.any { s -> s.id == dataGrade.subject.id }) subjects.add(addBsSubjectToDb(dataGrade.subject.id, dataGrade.subject.short, dataGrade.subject.name))
                    if (!teachers.any { t -> t.id == dataGrade.teacher.id }) teachers.add(addBsTeacherToDb(dataGrade.teacher.id, dataGrade.teacher.short, dataGrade.teacher.firstname, dataGrade.teacher.lastname))

                    val (gradeNumber, gradeModifier) = explodeGrade(dataGrade.value)

                    if (existingGrade == null || "${existingGrade.value.toInt()}${existingGrade.modifier}" != "$gradeNumber$gradeModifier") gradeDao.upsert(
                        DbGrade(
                            id = dataGrade.id,
                            vppId = vppId.id,
                            value = gradeNumber.toFloat(),
                            modifier = gradeModifier,
                            subject = subjects.first { it.id == dataGrade.subject.id }.id,
                            teacherId = teachers.first { it.id == dataGrade.teacher.id }.id,
                            givenAt = LocalDate.parse(dataGrade.givenAt),
                            type = dataGrade.collection.type,
                            comment = dataGrade.collection.name,
                            interval = dataGrade.collection.intervalId
                        )
                    )
                    if (existingGrade == null) {
                        val new = gradeDao.getGradeById(dataGrade.id).first().toModel()
                        existingGrades.add(new)
                        newGrades.add(new)
                    }
                }
            } catch (e: BsUnauthorizedException) {
                sendBsTokenInvalidNotification()
            } catch (e: BsRequestFailedException) {
                logRecordRepository.log("Grades", "Error: BS request failed with ${e.response?.value}")
            }
        }
        return newGrades
    }

    override fun getAllGrades(): Flow<List<Grade>> = flow {
        gradeDao.getAllGrades().collect {
            emit(it.map { g -> g.toModel() })
        }
    }

    override fun getGradesByUser(vppId: VppId): Flow<List<Grade>> {
        return gradeDao.getGradesByUser(vppId.id).map { it.map { grade -> grade.toModel() } }
    }

    override suspend fun dropAll() {
        gradeDao.dropAll()
    }

    /**
     * Updates the years and intervals in the database
     * @author Julius Babies
     * @param token the token to use for the request
     * @throws BsUnauthorizedException if the token is invalid
     * @throws BsRequestFailedException if the request failed
     */
    private suspend fun updateYears(token: String) {
        bsNetworkRepository.authentication = BearerAuthentication(token)
        val years = bsNetworkRepository.doRequest("/api/years").let {
            if (it.response == HttpStatusCode.Unauthorized) throw BsUnauthorizedException()
            if (it.response != HttpStatusCode.OK) throw BsRequestFailedException(it.response)
            Gson().fromJson(it.data, BsSchoolYearResponse::class.java).years
        }
        years.forEach year@{ year ->
            yearDao.upsert(
                DbYear(
                    id = year.id,
                    name = year.name,
                    from = year.start,
                    to = year.end
                )
            )
            year.intervals.forEach interval@{ interval ->
                yearDao.upsertInterval(
                    DbInterval(
                        id = interval.id,
                        name = interval.name,
                        type = interval.type,
                        from = interval.start,
                        to = interval.end,
                        includedIntervalId = interval.includedIntervalId,
                        yearId = year.id
                    )
                )
            }
        }
    }

    /**
     * Gets the grades from the server and returns them as a list of [BsGrade] objects
     * @param token the token to use for the request
     * @return the grades as a list of [BsGrade] objects
     * @throws BsUnauthorizedException if the token is invalid
     * @throws BsRequestFailedException if the request failed
     * @author Julius Babies
     * @see BsGrade
     */
    private suspend fun getRawGrades(token: String): List<BsGrade> {
        bsNetworkRepository.authentication = BearerAuthentication(token)
        val result = bsNetworkRepository.doRequest("/api/grades?include=collection")
        if (result.response == HttpStatusCode.Unauthorized) throw BsUnauthorizedException()
        if (result.response != HttpStatusCode.OK) throw BsRequestFailedException(result.response)
        return Gson().fromJson(result.data, BsGradesResponse::class.java).data
    }

    /**
     * Adds a [BsSubject] to the database
     * @param id the id of the subject
     * @param short the short name of the subject
     * @param name the name of the subject
     * @return the [Subject] object that was added to the database
     * @see Subject
     * @author Julius Babies
     */
    private fun addBsSubjectToDb(id: Long, short: String, name: String): Subject {
        val obj = DbSubject(id, short, name)
        subjectDao.insert(obj)
        return obj.toModel()
    }


    /**
     * Adds a [BsTeacher] to the database
     * @param id the id of the teacher
     * @param short the short name of the teacher
     * @param firstname the first name of the teacher
     * @param lastname the last name of the teacher
     * @return the [Teacher] object that was added to the database
     * @see Teacher
     * @author Julius Babies
     */
    private fun addBsTeacherToDb(id: Long, short: String, firstname: String, lastname: String): Teacher {
        val obj = DbTeacher(id, short, firstname, lastname)
        teacherDao.insert(obj)
        return obj.toModel()
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

    private suspend fun sendBsTokenInvalidNotification() {
        notificationRepository.sendNotification(
            CHANNEL_ID_GRADES,
            6000,
            stringRepository.getString(R.string.notification_gradeUnauthorizedTitle),
            stringRepository.getString(R.string.notification_gradeUnauthorizedContent),
            R.drawable.vpp,
            null
        )
    }
}

private data class BsGradesResponse(
    @SerializedName("data") val data: List<BsGrade>
)

private data class BsGrade(
    @SerializedName("id") val id: Long,
    @SerializedName("value") val value: String,
    @SerializedName("given_at") val givenAt: String,
    @SerializedName("subject") val subject: BsSubject,
    @SerializedName("teacher") val teacher: BsTeacher,
    @SerializedName("collection") val collection: BsCollection
)

private data class BsSubject(
    @SerializedName("id") val id: Long,
    @SerializedName("local_id") val short: String,
    @SerializedName("name") val name: String,
)

private data class BsTeacher(
    @SerializedName("id") val id: Long,
    @SerializedName("local_id") val short: String,
    @SerializedName("forename") val firstname: String,
    @SerializedName("name") val lastname: String
)

private data class BsCollection(
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("interval_id") val intervalId: Long
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

private class BsUnauthorizedException : Exception("The token is invalid")
private class BsRequestFailedException(val response: HttpStatusCode?) : Exception("The request failed with $response")