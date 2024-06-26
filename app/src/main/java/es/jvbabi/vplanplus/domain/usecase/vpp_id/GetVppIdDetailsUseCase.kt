package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.HOMEWORK_VPPID_BALLOON
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase
import kotlinx.coroutines.flow.first

class GetVppIdDetailsUseCase(
    private val vppIdRepository: VppIdRepository,
    private val setBalloonUseCase: SetBalloonUseCase
) {

    suspend operator fun invoke(code: String): VppId? {
        if (vppIdRepository.getActiveVppIds().first().isEmpty()) setBalloonUseCase(HOMEWORK_VPPID_BALLOON, true)
        return vppIdRepository.useOAuthCode(code)
    }
}