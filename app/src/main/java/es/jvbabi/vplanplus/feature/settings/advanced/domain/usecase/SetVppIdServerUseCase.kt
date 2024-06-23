package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.servers

class SetVppIdServerUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val systemRepository: SystemRepository
) {
    suspend operator fun invoke(vppIdServer: String?) {
        if (vppIdServer == servers.first().apiHost || vppIdServer.isNullOrBlank()) keyValueRepository.delete(Keys.VPPID_SERVER)
        else keyValueRepository.set(Keys.VPPID_SERVER, vppIdServer)
        systemRepository.closeApp()
    }
}