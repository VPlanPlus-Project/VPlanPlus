package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow

class IsShowNotificationOnNewHomeworkUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = flow {
        keyValueRepository.getFlowOrDefault(
            Keys.SHOW_NOTIFICATION_ON_NEW_HOMEWORK,
            Keys.SHOW_NOTIFICATION_ON_NEW_HOMEWORK_DEFAULT
        ).collect {
            emit(it.toBoolean())
        }
    }
}