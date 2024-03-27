package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.map

class GetCurrentDataVersionUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = keyValueRepository
        .getFlowOrDefault(Keys.LESSON_VERSION_NUMBER, "0")
        .map { it.toLong() }
}