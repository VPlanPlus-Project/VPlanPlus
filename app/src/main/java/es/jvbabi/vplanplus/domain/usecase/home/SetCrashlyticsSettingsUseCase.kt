package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class SetCrashlyticsSettingsUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.HAS_SET_CRASHLYTICS_SETTINGS, "true")
    }
}