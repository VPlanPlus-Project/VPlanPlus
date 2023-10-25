package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.repository.ClassRepository

class ClassUseCases(private val classRepository: ClassRepository) {

    /**
     * Creates a new class in the database
     * @param schoolId the id of the school the class belongs to
     * @param className the name of the class (e.g. 6c)
     */
    suspend fun createClass(schoolId: String, className: String) {
        classRepository.createClass(schoolId = schoolId, className = className)
    }

    suspend fun getClassIdBySchoolIdAndClassName(schoolId: String, className: String): Int {
        return classRepository.getClassIdBySchoolIdAndClassName(schoolId = schoolId, className = className)
    }

    suspend fun getClassById(id: Int): Classes {
        return classRepository.getClassById(id)
    }
}