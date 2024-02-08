package es.jvbabi.vplanplus.domain.usecase.settings.vpp_id

import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import kotlinx.coroutines.flow.map

class GetAccountsUseCase(
    private val vppIdRepository: VppIdRepository
) {
    operator fun invoke() = vppIdRepository.getVppIds().map { it.filter { vppId -> vppId.state != State.CACHE } }
}