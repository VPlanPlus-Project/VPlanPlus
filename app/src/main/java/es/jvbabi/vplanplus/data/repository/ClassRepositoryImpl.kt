package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.ClassDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.repository.ClassRepository

class ClassRepositoryImpl(private val classDao: ClassDao) : ClassRepository {
    override suspend fun createClass(schoolId: Long, className: String) {
        classDao.insertClass(Classes(schoolId = schoolId, className = className))
    }

    override suspend fun getClassBySchoolIdAndClassName(schoolId: Long, className: String, createIfNotExists: Boolean): Classes? {
        val `class` = classDao.getClassBySchoolIdAndClassName(schoolId = schoolId, className = className)
        if (`class` == null && createIfNotExists) {
            val id = classDao.insertClass(Classes(schoolId = schoolId, className = className))
            return classDao.getClassById(id = id)
        }
        return classDao.getClassBySchoolIdAndClassName(schoolId = schoolId, className = className)!!
    }

    override suspend fun getClassById(id: Long): Classes {
        return classDao.getClassById(id = id)
    }
}