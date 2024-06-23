package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class GetVppIdDetailsUseCase(
    private val vppIdRepository: VppIdRepository
) {

    suspend operator fun invoke(code: String): VppId? {
        return vppIdRepository.useOAuthCode(code)
    }
}