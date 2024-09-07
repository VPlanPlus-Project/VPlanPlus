package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class ToggleFcmDebugModeUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.FCM_DEBUG_MODE, keyValueRepository.getOrDefault(Keys.FCM_DEBUG_MODE, BuildConfig.DEBUG.toString()).toBoolean().not().toString())
    }
}