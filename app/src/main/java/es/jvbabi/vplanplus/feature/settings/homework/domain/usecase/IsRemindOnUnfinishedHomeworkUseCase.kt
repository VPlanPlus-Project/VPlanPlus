package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow

class IsRemindOnUnfinishedHomeworkUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = flow {
        keyValueRepository.getFlowOrDefault(
            Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK,
            Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_DEFAULT
        ).collect {
            emit(it.toBoolean())
        }
    }
}