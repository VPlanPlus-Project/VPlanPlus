package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.ClassDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import java.util.UUID

class ClassRepositoryImpl(private val classDao: ClassDao) : ClassRepository {
    override suspend fun createClass(schoolId: Long, className: String) {
        classDao.insertClass(DbClass(schoolClassRefId = schoolId, className = className))
    }

    override suspend fun getClassBySchoolIdAndClassName(schoolId: Long, className: String, createIfNotExists: Boolean): Classes {
        val `class` = classDao.getClassBySchoolIdAndClassName(schoolId = schoolId, className = className)
        if (`class` == null && createIfNotExists) {
            val dbClass = DbClass(
                classId = UUID.randomUUID(),
                schoolClassRefId = schoolId,
                className = className
            )
            classDao.insertClass(dbClass)
            return classDao.getClassById(id = dbClass.classId).toModel()
        }
        return classDao.getClassBySchoolIdAndClassName(schoolId = schoolId, className = className)!!.toModel()
    }

    override suspend fun getClassById(id: UUID): Classes {
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