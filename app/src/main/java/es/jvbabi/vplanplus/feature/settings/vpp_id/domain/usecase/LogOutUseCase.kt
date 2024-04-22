package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase

class LogOutUseCase(
    private val vppIdRepository: VppIdRepository,
    private val keyValueRepository: KeyValueRepository,
    private val testForMissingVppIdToProfileConnectionsUseCase: TestForMissingVppIdToProfileConnectionsUseCase
) {
    suspend operator fun invoke(vppId: VppId): Boolean {
        val result = vppIdRepository.unlinkVppId(vppId)
        keyValueRepository.set(Keys.MISSING_VPP_ID_TO_PROFILE_CONNECTION, testForMissingVppIdToProfileConnectionsUseCase().toString())
        return result
    }
}