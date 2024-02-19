package es.jvbabi.vplanplus.feature.grades.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.domain.model.VppId
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
    private val vppIdRepository: VppIdRepository
) : GradeRepository {
    override suspend fun updateGrades() {
        val vppIds = vppIdRepository.getVppIds().first()
        vppIds.forEach vppId@{ vppId ->
            val bsToken = vppIdRepository.getBsToken(vppId) ?: return@vppId

            bsNetworkRepository.authentication = TokenAuthentication("Bearer ", bsToken)
            val result = bsNetworkRepository.doRequest(
                "/api/grades?include=collection"
            )
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
            }
        }
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

data class BsGradesResponse(
    val data: List<BsGrade>
)

data class BsGrade(
    val id: Long,
    val value: String,
    @SerializedName("given_at") val givenAt: String,
    val subject: BsSubject,
    val teacher: BsTeacher,
    val collection: BsCollection
)

data class BsSubject(
    val id: Long,
    @SerializedName("local_id") val short: String,
    val name: String,
)

data class BsTeacher(
    val id: Long,
    @SerializedName("local_id") val short: String,
    @SerializedName("forename") val firstname: String,
    @SerializedName("name") val lastname: String
)

data class BsCollection(
    val type: String,
    val name: String
)