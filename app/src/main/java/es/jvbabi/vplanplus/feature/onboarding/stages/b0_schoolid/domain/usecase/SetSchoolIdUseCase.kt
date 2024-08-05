package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.domain.usecase.onboardingSetupKeys

class SetSchoolIdUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val schoolRepository: SchoolRepository
) {
    suspend operator fun invoke(sp24SchoolId: Int) {
        keyValueRepository.get("onboarding.sp24_school_id")?.toIntOrNull()?.let { canceledSetupSp24Id ->
            val school = schoolRepository.getSchoolBySp24Id(canceledSetupSp24Id)
            if (school != null) schoolRepository.deleteSchool(school.id)
        }
        onboardingSetupKeys.forEach { keyValueRepository.delete(it) }
        keyValueRepository.set("onboarding.sp24_school_id", sp24SchoolId.toString())
    }
}