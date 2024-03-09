package es.jvbabi.vplanplus.domain.usecase.settings.advanced

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.SystemRepository

class SetVppIdServerUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val systemRepository: SystemRepository
) {
    suspend operator fun invoke(vppIdServer: String?) {
        if (vppIdServer == Keys.VPPID_SERVER_DEFAULT || vppIdServer.isNullOrBlank()) keyValueRepository.delete(Keys.VPPID_SERVER)
        else keyValueRepository.set(Keys.VPPID_SERVER, vppIdServer)
        systemRepository.closeApp()
    }
}