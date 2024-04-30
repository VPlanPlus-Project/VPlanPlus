package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.map

class HasMissingVppIdToProfileLinksUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = keyValueRepository.getFlowOrDefault(Keys.MISSING_VPP_ID_TO_PROFILE_CONNECTION, "false").map { it.toBoolean() }
}