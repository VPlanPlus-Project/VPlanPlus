package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.TeacherDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.repository.TeacherRepository

class TeacherRepositoryImpl(
    private val teacherDao: TeacherDao
): TeacherRepository {
    override suspend fun createTeacher(schoolId: Long, acronym: String) {
        teacherDao.insertTeacher(Teacher(schoolId = schoolId, acronym = acronym))
    }

    override suspend fun getTeachersBySchoolId(schoolId: Long): List<Teacher> {
        TODO("Not yet implemented")
    }

    override suspend fun find(school: School, acronym: String, createIfNotExists: Boolean): Teacher? {
        val teacher = teacherDao.find(school.id!!, acronym)
        if (teacher == null && createIfNotExists && acronym.isNotBlank()) {
            val id = teacherDao.insertTeacher(Teacher(schoolId = school.id, acronym = acronym))
            return teacherDao.getTeacherById(id)
        }
        return teacher
    }

    override suspend fun getTeacherById(id: Long): Teacher? {
        return teacherDao.getTeacherById(id)
    }
}