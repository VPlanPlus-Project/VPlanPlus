package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.feature.settings.general.domain.data.AppThemeMode

class GetAppThemeUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = keyValueRepository.getFlowOrDefault(Keys.APP_THEME_MODE, AppThemeMode.SYSTEM.name)
}