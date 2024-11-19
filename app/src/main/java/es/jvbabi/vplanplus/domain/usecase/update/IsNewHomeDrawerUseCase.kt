package es.jvbabi.vplanplus.domain.usecase.update

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.map

class IsNewHomeDrawerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = keyValueRepository.getFlow(Keys.SHOW_NEW_HOME_DRAWER).map { it.toBoolean() }
}