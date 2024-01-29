package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class GetVppIdDetailsUseCase(
    private val vppIdRepository: VppIdRepository
) {

    suspend operator fun invoke(token: String): DataResponse<VppId?> {
        val response = vppIdRepository.getVppIdOnline(token)
        if (response.data != null) {
            vppIdRepository.addVppId(response.data)
        }
        return response
    }
}