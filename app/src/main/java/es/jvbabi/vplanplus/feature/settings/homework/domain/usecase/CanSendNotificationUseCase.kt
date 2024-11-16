package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class CanSendNotificationUseCase(
    private val alarmManagerRepository: AlarmManagerRepository
) {
    operator fun invoke() = flow {
        while (true) {
            emit(alarmManagerRepository.canRequestAlarm())
            kotlinx.coroutines.delay(500)
        }
    }.distinctUntilChanged()
}