package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.map

class GetHideFinishedLessonsUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = keyValueRepository
        .getFlowOrDefault(Keys.HIDE_FINISHED_LESSONS, "false")
        .map { it.toBoolean() }
}