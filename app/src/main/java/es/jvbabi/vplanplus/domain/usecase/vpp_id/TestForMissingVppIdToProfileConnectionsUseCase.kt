package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import kotlinx.coroutines.flow.first

class TestForMissingVppIdToProfileConnectionsUseCase(
    private val vppIdRepository: VppIdRepository,
    private val profileRepository: ProfileRepository
) {

    /**
     * Checks if there are any VppIds that are not connected to a profile
     * @return true if there are any VppIds that are not connected to a profile, false otherwise.
     */
    suspend operator fun invoke(): Boolean {
        val vppIds = vppIdRepository.getVppIds().first().filter { it.isActive() }
        val withProfileConnectedVppIds = profileRepository.getProfiles().first().mapNotNull { it.vppId }.distinctBy { vppId -> vppId.id }
        return vppIds.any { it !in withProfileConnectedVppIds }
    }
}