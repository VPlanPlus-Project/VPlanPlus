package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.ClassDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.repository.ClassRepository

class ClassRepositoryImpl(private val classDao: ClassDao) : ClassRepository {
    override suspend fun createClass(schoolId: String, className: String) {
        classDao.insertClass(Classes(schoolId = schoolId, className = className))
    }

    override suspend fun getClassIdBySchoolIdAndClassName(schoolId: String, className: String): Int {
        return classDao.getClassIdBySchoolIdAndClassName(schoolId = schoolId, className = className)
    }
}