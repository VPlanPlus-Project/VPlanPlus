package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.map

class IsFcmDebugModeUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() =
        keyValueRepository.getFlowOrDefault(Keys.FCM_DEBUG_MODE, BuildConfig.DEBUG.toString()).map {
            it.toBoolean()
        }
}