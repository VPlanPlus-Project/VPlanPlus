package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.TeacherDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.xml.DefaultValues
import es.jvbabi.vplanplus.domain.repository.TeacherRepository

class TeacherRepositoryImpl(
    private val teacherDao: TeacherDao
): TeacherRepository {
    override suspend fun createTeacher(schoolId: Long, acronym: String) {
        teacherDao.insertTeacher(Teacher(schoolId = schoolId, acronym = acronym))
    }

    override suspend fun getTeachersBySchoolId(schoolId: Long): List<Teacher> {
        return teacherDao.getTeachersBySchoolId(schoolId = schoolId)
    }

    override suspend fun find(school: School, acronym: String, createIfNotExists: Boolean): Teacher? {
        if (DefaultValues.isEmpty(acronym)) return null
        val teacher = teacherDao.find(school.id!!, acronym)
        if (teacher == null && createIfNotExists && acronym.isNotBlank()) {
            val id = teacherDao.insertTeacher(Teacher(schoolId = school.id, acronym = acronym))
            return teacherDao.getTeacherById(id)
        }
        return teacher
    }

    override fun getTeacherById(id: Long): Teacher? {
        return teacherDao.getTeacherById(id)
    }

    override suspend fun deleteTeachersBySchoolId(schoolId: Long) {
        teacherDao.deleteTeachersBySchoolId(schoolId)
    }

    override suspend fun insertTeachersByAcronym(schoolId: Long, teachers: List<String>) {
        teachers.forEach {
            createTeacher(schoolId, it)
        }
    }
}