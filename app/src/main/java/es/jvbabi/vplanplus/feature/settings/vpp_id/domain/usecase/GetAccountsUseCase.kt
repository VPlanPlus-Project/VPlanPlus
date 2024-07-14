package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase

import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class GetAccountsUseCase(
    private val vppIdRepository: VppIdRepository
) {
    operator fun invoke() = vppIdRepository.getActiveVppIds()
}