package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class ToggleShowNotificationOnNewHomeworkUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.SHOW_NOTIFICATION_ON_NEW_HOMEWORK, (keyValueRepository.getOrDefault(Keys.SHOW_NOTIFICATION_ON_NEW_HOMEWORK, Keys.SHOW_NOTIFICATION_ON_NEW_HOMEWORK_DEFAULT).toBoolean().not()).toString())
    }
}