package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.model.Session

class CloseSessionUseCase(
    private val vppIdRepository: VppIdRepository
) {

    suspend operator fun invoke(session: Session, vppId: VppId): Boolean {
        return vppIdRepository.closeSession(session, vppId)
    }
}