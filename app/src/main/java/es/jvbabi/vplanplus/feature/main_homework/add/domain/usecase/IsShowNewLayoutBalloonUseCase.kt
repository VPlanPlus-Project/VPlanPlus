package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow

class IsShowNewLayoutBalloonUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() = flow {
        keyValueRepository.getFlow(Keys.ADD_HOMEWORK_SHOW_NEW_LAYOUT_BALLOON).collect {
            emit(it == "true")
        }
    }
}