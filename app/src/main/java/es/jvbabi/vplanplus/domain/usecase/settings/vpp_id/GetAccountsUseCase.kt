package es.jvbabi.vplanplus.domain.usecase.settings.vpp_id

import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class GetAccountsUseCase(
    private val vppIdRepository: VppIdRepository
) {
    operator fun invoke() = vppIdRepository.getVppIds()
}