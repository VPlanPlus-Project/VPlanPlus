package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.HOMEWORK_VPPID_BALLOON
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase
import kotlinx.coroutines.flow.first

class GetVppIdDetailsUseCase(
    private val vppIdRepository: VppIdRepository,
    private val firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository,
    private val keyValueRepository: KeyValueRepository,
    private val setBalloonUseCase: SetBalloonUseCase
) {

    suspend operator fun invoke(code: String): VppId.ActiveVppId? {
        if (vppIdRepository.getActiveVppIds().first().isEmpty()) setBalloonUseCase(HOMEWORK_VPPID_BALLOON, true)
        val vppId = vppIdRepository.useOAuthCode(code)
        if (vppId != null) {
            firebaseCloudMessagingManagerRepository.addTokenUser(vppId, keyValueRepository.get(Keys.FCM_TOKEN) ?: "")
        }
        return vppId
    }
}