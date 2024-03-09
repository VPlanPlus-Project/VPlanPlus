package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import java.util.UUID

class ClassRepositoryImpl(
    private val schoolEntityDao: SchoolEntityDao,
) : ClassRepository {
    override suspend fun createClass(schoolId: Long, className: String) {
        schoolEntityDao.insertSchoolEntity(
            DbSchoolEntity(
                schoolId = schoolId,
                name = className,
                type = SchoolEntityType.CLASS
            )
        )
    }

    override suspend fun getClassBySchoolIdAndClassName(
        schoolId: Long,
        className: String,
        createIfNotExists: Boolean
    ): Classes {
        val `class` = schoolEntityDao.getSchoolEntityByName(
            schoolId = schoolId,
            name = className,
            type = SchoolEntityType.CLASS
        )
        if (`class` == null && createIfNotExists) {
            val dbClass = DbSchoolEntity(
                id = UUID.randomUUID(),
                schoolId = schoolId,
                name = className,
                type = SchoolEntityType.CLASS
            )
            schoolEntityDao.insertSchoolEntity(dbClass)
            return schoolEntityDao.getSchoolEntityById(dbClass.id)!!.toClassModel()
        }
        return schoolEntityDao.getSchoolEntityByName(
            schoolId = schoolId,
            name = className,
            type = SchoolEntityType.CLASS
        )!!.toClassModel()
    }

    override suspend fun getClassById(id: UUID): Classes? {
        return schoolEntityDao.getSchoolEntityById(id)?.toClassModel()
    }

    override suspend fun insertClasses(schoolId: Long, classes: List<String>) {
        schoolEntityDao.insertSchoolEntities(classes.map {
            DbSchoolEntity(
                id = UUID.randomUUID(),
                schoolId = schoolId,
                name = it,
                type = SchoolEntityType.CLASS
            )
        })
    }

    override suspend fun deleteClassesBySchoolId(schoolId: Long) {
        schoolEntityDao.deleteSchoolEntitiesBySchoolId(
            schoolId = schoolId,
            type = SchoolEntityType.CLASS
        )
    }

    override suspend fun getClassesBySchool(school: School): List<Classes> {
        return schoolEntityDao.getSchoolEntities(
            schoolId = school.schoolId,
            type = SchoolEntityType.CLASS
        ).map { it.toClassModel() }
    }
}