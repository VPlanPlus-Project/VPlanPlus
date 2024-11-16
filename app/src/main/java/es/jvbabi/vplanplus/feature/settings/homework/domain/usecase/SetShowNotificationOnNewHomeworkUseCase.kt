package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class SetShowNotificationOnNewHomeworkUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(show: Boolean) {
        keyValueRepository.set(Keys.SHOW_NOTIFICATION_ON_NEW_HOMEWORK, show.toString())
    }
}