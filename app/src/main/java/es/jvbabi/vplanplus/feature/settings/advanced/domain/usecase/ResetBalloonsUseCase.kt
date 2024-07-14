package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.general.BALLOONS

class ResetBalloonsUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        BALLOONS.forEach { balloon ->
            keyValueRepository.delete("BALLOON_" + balloon.name)
        }
    }
}