package es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository

class GetSp24SchoolIdUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(): Int {
        return keyValueRepository.get("onboarding.sp24_school_id")?.toInt() ?: 0
    }
}