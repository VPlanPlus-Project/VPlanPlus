package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class TestAccountUseCase(
    private val vppIdRepository: VppIdRepository
) {
    suspend operator fun invoke(vppId: VppId): Boolean? {
        return vppIdRepository.testVppId(vppId)
    }
}