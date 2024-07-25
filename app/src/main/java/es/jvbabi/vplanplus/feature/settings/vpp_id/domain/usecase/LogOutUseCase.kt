package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.UpdateFcmTokenUseCase

class LogOutUseCase(
    private val vppIdRepository: VppIdRepository,
    private val keyValueRepository: KeyValueRepository,
    private val testForMissingVppIdToProfileConnectionsUseCase: TestForMissingVppIdToProfileConnectionsUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) {
    suspend operator fun invoke(vppId: VppId.ActiveVppId): Boolean {
        val result = vppIdRepository.unlinkVppId(vppId)
        updateFcmTokenUseCase()
        keyValueRepository.set(Keys.MISSING_VPP_ID_TO_PROFILE_CONNECTION, testForMissingVppIdToProfileConnectionsUseCase().toString())
        return result
    }
}