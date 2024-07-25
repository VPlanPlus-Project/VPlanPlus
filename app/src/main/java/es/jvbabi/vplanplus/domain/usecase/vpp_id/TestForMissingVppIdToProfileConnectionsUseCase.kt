package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
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
    suspend operator fun invoke(autoFix: Boolean = false): Boolean {
        val vppIds = vppIdRepository.getActiveVppIds().first()
        val withProfileConnectedVppIds = profileRepository.getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .mapNotNull { it.vppId }
            .distinctBy { vppId -> vppId.id }
        if (autoFix) {
            val profiles = profileRepository.getProfiles().first().filterIsInstance<ClassProfile>()
            val resolveMap = mutableMapOf<ClassProfile, VppId.ActiveVppId>()
            vppIds
                .filter { vppId -> profiles.none { it.vppId == vppId} } // every vppId that is not connected to a profile
                .forEach { vppId ->
                    val matchingProfiles = profiles.filter { profile -> profile.group.groupId == vppId.group?.groupId && profile.vppId == null }
                    if (matchingProfiles.size == 1 && resolveMap[matchingProfiles[0]] == null) {
                        resolveMap[matchingProfiles[0]] = vppId
                    }
                }
            resolveMap.forEach { (profile, vppId) ->
                profileRepository.setVppIdForProfile(profile, vppId)
            }
            return invoke(false)
        }
        return vppIds.any { it !in withProfileConnectedVppIds }
    }
}