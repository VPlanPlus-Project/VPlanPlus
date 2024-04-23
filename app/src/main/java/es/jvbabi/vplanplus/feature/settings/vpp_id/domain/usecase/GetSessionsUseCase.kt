package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class GetSessionsUseCase(
    private val vppIdRepository: VppIdRepository
) {

    suspend operator fun invoke(vppId: VppId) = vppIdRepository.fetchSessions(vppId)
}