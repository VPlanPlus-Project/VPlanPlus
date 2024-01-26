package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.xml.DefaultValues
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import java.util.UUID

class TeacherRepositoryImpl(
    private val schoolEntityDao: SchoolEntityDao
): TeacherRepository {
    override suspend fun createTeacher(schoolId: Long, acronym: String) {
        schoolEntityDao.insertSchoolEntity(
            DbSchoolEntity(
                id = UUID.randomUUID(),
                schoolId = schoolId,
                name = acronym,
                type = SchoolEntityType.TEACHER
            )
        )
    }

    override suspend fun getTeachersBySchoolId(schoolId: Long): List<Teacher> {
        return schoolEntityDao.getSchoolEntities(schoolId, SchoolEntityType.TEACHER).map { it.toTeacherModel() }
    }

    override suspend fun find(school: School, acronym: String, createIfNotExists: Boolean): Teacher? {
        if (DefaultValues.isEmpty(acronym)) return null
        val teacher = schoolEntityDao.getSchoolEntityByName(school.schoolId, acronym, SchoolEntityType.TEACHER)
        if (teacher == null && createIfNotExists && acronym.isNotBlank()) {
            val dbTeacher = DbSchoolEntity(
                id = UUID.randomUUID(),
                schoolId = school.schoolId,
                name = acronym,
                type = SchoolEntityType.TEACHER
            )
            schoolEntityDao.insertSchoolEntity(dbTeacher)
            return schoolEntityDao.getSchoolEntityById(dbTeacher.id)!!.toTeacherModel()
        }
        return teacher?.toTeacherModel()
    }

    override suspend fun getTeacherById(id: UUID): Teacher? {
        return schoolEntityDao.getSchoolEntityById(id)?.toTeacherModel()
    }

    override suspend fun deleteTeachersBySchoolId(schoolId: Long) {
        schoolEntityDao.deleteSchoolEntitiesBySchoolId(schoolId, SchoolEntityType.TEACHER)
    }

    override suspend fun insertTeachersByAcronym(schoolId: Long, teachers: List<String>) {
        schoolEntityDao.insertSchoolEntities(
            teachers.map {
                DbSchoolEntity(
                    id = UUID.randomUUID(),
                    schoolId = schoolId,
                    name = it,
                    type = SchoolEntityType.TEACHER
                )
            }
        )
    }
}