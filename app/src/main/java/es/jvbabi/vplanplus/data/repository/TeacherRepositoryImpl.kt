package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.TeacherDao
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.repository.TeacherRepository

class TeacherRepositoryImpl(
    private val teacherDao: TeacherDao
): TeacherRepository {
    override suspend fun createTeacher(schoolId: String, acronym: String) {
        teacherDao.insertTeacher(Teacher(schoolId = schoolId, acronym = acronym))
    }

    override suspend fun getTeachersBySchoolId(schoolId: String): List<Teacher> {
        TODO("Not yet implemented")
    }

    override suspend fun find(schoolId: String, acronym: String): Teacher? {
        return teacherDao.find(schoolId, acronym)
    }

    override suspend fun getTeacherById(id: Int): Teacher? {
        return teacherDao.getTeacherById(id)
    }
}