package es.jvbabi.vplanplus.feature.homework.view.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow

class ShowHomeworkNotificationBannerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = flow {
        keyValueRepository.getFlowOrDefault(Keys.SHOW_HOMEWORK_NOTIFICATION_BANNER, "true").collect {
            emit(it.toBoolean())
        }
    }
}