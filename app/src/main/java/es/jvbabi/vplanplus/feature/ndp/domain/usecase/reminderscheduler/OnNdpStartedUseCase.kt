package es.jvbabi.vplanplus.feature.ndp.domain.usecase.reminderscheduler

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.repository.NdpUsageRepository
import kotlinx.coroutines.flow.first

class OnNdpStartedUseCase(
    private val ndpUsageRepository: NdpUsageRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke() {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return
        ndpUsageRepository.startNdp(profile)
    }
}