package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase

class SetDefaultNotificationTimeUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val setUpUseCase: SetUpUseCase
) {
    suspend operator fun invoke(hour: Int, minute: Int) {
        val time = (hour * 60 + minute) * 60
        keyValueRepository.set(Keys.SETTINGS_PREFERRED_NOTIFICATION_TIME, time.toString())
        setUpUseCase.createHomeworkReminder()
    }
}