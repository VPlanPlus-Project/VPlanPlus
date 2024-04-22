package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class UpdateMissingLinksStateUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val testForMissingVppIdToProfileConnectionsUseCase: TestForMissingVppIdToProfileConnectionsUseCase
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.MISSING_VPP_ID_TO_PROFILE_CONNECTION, testForMissingVppIdToProfileConnectionsUseCase().toString())
    }
}