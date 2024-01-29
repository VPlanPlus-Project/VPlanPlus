package es.jvbabi.vplanplus.domain.usecase.vpp_id

import android.util.Log
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.repository.ClassRepository

class GetClassUseCase(
    private val classRepository: ClassRepository
){
    suspend operator fun invoke(schoolId: Long, className: String): Classes? {
        Log.d("GetClassUseCase", "invoke: $schoolId, $className")
        return classRepository.getClassBySchoolIdAndClassName(schoolId, className)
    }
}