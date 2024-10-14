package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class SetAutomaticReminderTimeEnabledUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        keyValueRepository.set(Keys.SETTINGS_NEXT_DAY_PREP_TIME_AUTOMATIC, enabled.toString())
    }
}