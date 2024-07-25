package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.servers
import kotlinx.coroutines.flow.flow

class GetVppIdServerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = flow {
        keyValueRepository.getFlowOrDefault(Keys.VPPID_SERVER, servers.first().apiHost).collect { apiHost ->
            emit(servers.first { it.apiHost == apiHost })
        }
    }
}