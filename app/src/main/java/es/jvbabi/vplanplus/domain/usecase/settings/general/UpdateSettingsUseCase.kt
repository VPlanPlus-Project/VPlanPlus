package es.jvbabi.vplanplus.domain.usecase.settings.general

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class UpdateSettingsUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(settings: GeneralSettings) {
        keyValueRepository.set(Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE, settings.showNotificationsIfAppIsVisible.toString())
        keyValueRepository.set(Keys.SETTINGS_SYNC_DAY_DIFFERENCE, settings.daysAheadSync.toString())
        keyValueRepository.set(Keys.COLOR, settings.colorScheme.entries.firstOrNull { it.value.active }?.key?.ordinal.toString())
    }
}