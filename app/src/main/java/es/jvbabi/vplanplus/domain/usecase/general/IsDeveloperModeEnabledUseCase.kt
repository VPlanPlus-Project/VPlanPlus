package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.map

class IsDeveloperModeEnabledUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = keyValueRepository.getFlow(Keys.APP_DEVELOPER_MODE).map { it.toBoolean() }
}