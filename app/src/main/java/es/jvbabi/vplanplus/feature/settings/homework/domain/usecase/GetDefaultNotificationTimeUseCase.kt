package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.ZoneOffset

class GetDefaultNotificationTimeUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = flow {
        keyValueRepository.getFlowOrDefault(Keys.SETTINGS_PREFERRED_NOTIFICATION_TIME, Keys.SETTINGS_PREFERRED_NOTIFICATION_TIME_DEFAULT.toString()).collect {
            val time = it.toLong()
            val date = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC)
            emit(date)
        }
    }
}