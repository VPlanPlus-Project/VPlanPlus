package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbClass
import es.jvbabi.vplanplus.data.source.database.dao.ClassDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository

class ClassRepositoryImpl(private val classDao: ClassDao) : ClassRepository {
    override suspend fun createClass(schoolId: Long, className: String) {
        classDao.insertClass(DbClass(schoolClassRefId = schoolId, className = className))
    }

    override suspend fun getClassBySchoolIdAndClassName(schoolId: Long, className: String, createIfNotExists: Boolean): Classes {
        val `class` = classDao.getClassBySchoolIdAndClassName(schoolId = schoolId, className = className)
        if (`class` == null && createIfNotExists) {
            val id = classDao.insertClass(DbClass(schoolClassRefId = schoolId, className = className))
            return classDao.getClassById(id = id).toModel()
        }
        return classDao.getClassBySchoolIdAndClassName(schoolId = schoolId, className = className)!!.toModel()
    }

    override fun getClassById(id: Long): Classes {
        return classDao.getClassById(id = id).toModel()
    }

    override suspend fun insertClasses(schoolId: Long, classes: List<String>) {
        classes.forEach { className ->
            classDao.insertClass(DbClass(schoolClassRefId = schoolId, className = className))
        }
    }

    override suspend fun deleteClassesBySchoolId(schoolId: Long) {
        classDao.deleteClassesBySchoolId(schoolId = schoolId)
    }

    override suspend fun getClassesBySchool(school: School): List<Classes> {
        return classDao.getClassesBySchoolId(schoolId = school.schoolId).map { it.toModel() }
    }
}