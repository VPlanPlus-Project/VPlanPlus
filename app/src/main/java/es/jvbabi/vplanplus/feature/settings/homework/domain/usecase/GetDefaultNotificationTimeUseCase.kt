package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow

class GetDefaultNotificationTimeUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = flow {
        keyValueRepository.getFlowOrDefault(Keys.SETTINGS_PREFERRED_NOTIFICATION_TIME, Keys.SETTINGS_PREFERRED_NOTIFICATION_TIME_DEFAULT.toString()).collect {
            emit(it.toLong())
        }
    }
}