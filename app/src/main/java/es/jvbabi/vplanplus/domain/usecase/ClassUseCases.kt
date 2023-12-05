package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import java.util.UUID

class ClassUseCases(private val classRepository: ClassRepository) {

    suspend fun getClassBySchoolIdAndClassName(schoolId: Long, className: String): Classes? {
        return classRepository.getClassBySchoolIdAndClassName(schoolId = schoolId, className = className)
    }

    suspend fun getClassById(id: UUID): Classes {
        return classRepository.getClassById(id)
    }

    suspend fun getClassesBySchool(school: School): List<Classes> {
        return classRepository.getClassesBySchool(school)
    }
}