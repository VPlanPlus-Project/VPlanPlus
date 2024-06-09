package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow

class IsUpdateRunningUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = flow {
        keyValueRepository.getFlow(Keys.IS_HOMEWORK_UPDATE_RUNNING).collect {
            emit(it?.toBoolean() ?: false)
        }
    }
}