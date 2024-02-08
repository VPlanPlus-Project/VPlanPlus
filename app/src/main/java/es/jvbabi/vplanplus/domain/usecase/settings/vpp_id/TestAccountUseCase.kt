package es.jvbabi.vplanplus.domain.usecase.settings.vpp_id

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class TestAccountUseCase(
    private val vppIdRepository: VppIdRepository
) {
    suspend operator fun invoke(vppId: VppId): DataResponse<Boolean?> {
        return vppIdRepository.testVppId(vppId)
    }
}