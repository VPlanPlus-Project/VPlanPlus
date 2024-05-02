package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import java.util.UUID

class FakeClassRepository(
    private val schoolRepository: FakeSchoolRepository
) : ClassRepository {
    private val classes = mutableListOf<Classes>()

    override suspend fun createClass(schoolId: Long, className: String) {
        classes.add(Classes(classId = UUID.randomUUID(), school = schoolRepository.getSchoolFromId(schoolId)!!, name = className))
    }

    override suspend fun getClassBySchoolIdAndClassName(
        schoolId: Long,
        className: String,
        createIfNotExists: Boolean
    ): Classes? {
        return classes.firstOrNull { it.school.schoolId == schoolId && it.name == className }
    }

    override suspend fun getClassById(id: UUID): Classes? {
        return classes.firstOrNull { it.classId == id }
    }

    override suspend fun insertClasses(schoolId: Long, classes: List<String>) {
        classes.forEach { createClass(schoolId, it) }
    }

    override suspend fun deleteClassesBySchoolId(schoolId: Long) {
        classes.removeIf { it.school.schoolId == schoolId }
    }

    override suspend fun getClassesBySchool(school: School): List<Classes> {
        return classes.filter { it.school == school }
    }

    override suspend fun getAll(): List<Classes> {
        return classes
    }

    companion object {
        val classNames = listOf(
            "5a",
            "5b",
            "5c",
            "6a",
            "6b",
            "6c",
            "7a",
            "7b",
            "7c",
            "8a",
            "8b",
            "8c",
            "9a",
            "9b",
            "9c",
            "10a",
            "10b",
            "10c",
            "JG11",
            "JG12"
        )
    }
}