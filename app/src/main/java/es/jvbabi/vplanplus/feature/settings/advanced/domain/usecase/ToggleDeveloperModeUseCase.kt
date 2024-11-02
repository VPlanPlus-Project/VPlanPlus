package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.usecase.general.IsDeveloperModeEnabledUseCase
import kotlinx.coroutines.flow.first

class ToggleDeveloperModeUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val isDeveloperModeEnabledUseCase: IsDeveloperModeEnabledUseCase
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.APP_DEVELOPER_MODE, isDeveloperModeEnabledUseCase().first().not().toString())
    }
}