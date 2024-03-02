package es.jvbabi.vplanplus.feature.grades.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_GRADES
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.grades.data.model.DbGrade
import es.jvbabi.vplanplus.feature.grades.data.model.DbSubject
import es.jvbabi.vplanplus.feature.grades.data.model.DbTeacher
import es.jvbabi.vplanplus.feature.grades.data.source.database.GradeDao
import es.jvbabi.vplanplus.feature.grades.data.source.database.SubjectDao
import es.jvbabi.vplanplus.feature.grades.data.source.database.TeacherDao
import es.jvbabi.vplanplus.feature.grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.grades.domain.model.GradeModifier
import es.jvbabi.vplanplus.feature.grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.shared.data.BsNetworkRepository
import es.jvbabi.vplanplus.shared.data.TokenAuthentication
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
    private val bsNetworkRepository: BsNetworkRepository,
    private val vppIdRepository: VppIdRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository
) : GradeRepository {
    override suspend fun updateGrades(): List<Grade> {
        val vppIds = vppIdRepository.getVppIds().first()
        val newGrades = mutableListOf<Grade>()
        vppIds.forEach vppId@{ vppId ->
            val bsToken = vppIdRepository.getBsToken(vppId) ?: return@vppId

            bsNetworkRepository.authentication = TokenAuthentication("Bearer ", bsToken)
            val result = bsNetworkRepository.doRequest(
                "/api/grades?include=collection"
            )
            if (result.response == HttpStatusCode.Unauthorized) {
                notificationRepository.sendNotification(
                    CHANNEL_ID_GRADES,
                    6000,
                    stringRepository.getString(R.string.notification_gradeUnauthorizedTitle),
                    stringRepository.getString(R.string.notification_gradeUnauthorizedContent),
                    R.drawable.vpp,
                    null
                )
                return@vppId
            }
            if (result.response != HttpStatusCode.OK) return@vppId
            val data = Gson().fromJson(result.data, BsGradesResponse::class.java).data

            var subjects = subjectDao.getSubjects().first().map { it.toModel() }
            var teachers = teacherDao.getAllTeachers().first().map { it.toModel() }
            var grades = gradeDao.getAllGrades().first().map { it.toModel() }

            data.forEach { grade ->
                if (grades.any { g -> g.id == grade.id }) return@forEach
                if (!subjects.any { s -> s.id == grade.subject.id }) {
                    subjectDao.insert(
                        DbSubject(
                            id = grade.subject.id,
                            short = grade.subject.short,
                            name = grade.subject.name
                        )
                    )
                    subjects = subjectDao.getSubjects().first().map { it.toModel() }
                }
                if (!teachers.any { t -> t.id == grade.teacher.id }) {
                    teacherDao.insert(
                        DbTeacher(
                            id = grade.teacher.id,
                            short = grade.teacher.short,
                            firstname = grade.teacher.firstname,
                            lastname = grade.teacher.lastname
                        )
                    )
                    teachers = teacherDao.getAllTeachers().first().map { it.toModel() }
                }

                val regex = "(\\d+)([-+]?)".toRegex()
                val matchResult =
                    regex.find(grade.value)
                val (number, modifier) = matchResult?.destructured ?: return@forEach
                val gradeNumber = number.toInt()

                val gradeModifier = when (modifier) {
                    "+" -> GradeModifier.PLUS
                    "-" -> GradeModifier.MINUS
                    else -> GradeModifier.NEUTRAL
                }
                gradeDao.insert(
                    DbGrade(
                        id = grade.id,
                        vppId = vppId.id,
                        value = gradeNumber.toFloat(),
                        modifier = gradeModifier,
                        subject = subjects.first { it.id == grade.subject.id }.id,
                        givenBy = teachers.first { it.id == grade.teacher.id }.id,
                        givenAt = LocalDate.parse(grade.givenAt),
                        type = grade.collection.type,
                        comment = grade.collection.name
                    )
                )
                grades = gradeDao.getAllGrades().first().map { it.toModel() }
                newGrades.add(grades.first { it.id == grade.id })
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
        return gradeDao.getGrades(vppId.id).map { it.map { grade -> grade.toModel() } }
    }

    override suspend fun dropAll() {
        gradeDao.dropAll()
    }
}

private data class BsGradesResponse(
    val data: List<BsGrade>
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
    @SerializedName("name") val name: String
)