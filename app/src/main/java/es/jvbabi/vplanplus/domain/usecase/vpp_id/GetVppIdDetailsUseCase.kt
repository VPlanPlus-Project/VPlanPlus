package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.repository.VppIdOnlineResponse
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class GetVppIdDetailsUseCase(
    private val vppIdRepository: VppIdRepository
) {

    suspend operator fun invoke(token: String): DataResponse<VppIdOnlineResponse?> {
        val response = vppIdRepository.getVppIdOnline(token)
        if (response.data != null) {
            vppIdRepository.addVppId(response.data.id)
            vppIdRepository.addVppIdToken(response.data.id, token, response.data.bsToken)
        }
        return response
    }
}