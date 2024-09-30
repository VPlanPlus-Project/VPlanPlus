package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.map

class HasSetCrashlyticsSettingsUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = keyValueRepository.getFlowOrDefault(Keys.HAS_SET_CRASHLYTICS_SETTINGS, "false").map {
        it.toBoolean()
    }
}