package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository

class SetSchoolIdUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(sp24SchoolId: Int) {
        keyValueRepository.set("onboarding.sp24_school_id", sp24SchoolId.toString())
    }
}