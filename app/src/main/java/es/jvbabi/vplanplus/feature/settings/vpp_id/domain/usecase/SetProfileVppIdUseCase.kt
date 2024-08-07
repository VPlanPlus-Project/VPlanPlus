package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase

class SetProfileVppIdUseCase(
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository,
    private val testForMissingVppIdToProfileConnectionsUseCase: TestForMissingVppIdToProfileConnectionsUseCase
) {
    suspend operator fun invoke(profiles: Map<ClassProfile, Boolean>, vppId: VppId.ActiveVppId) {
        profiles.forEach { (profile, isSelected) -> profileRepository.setVppIdForProfile(profile, if (isSelected) vppId else null) }
        keyValueRepository.set(Keys.MISSING_VPP_ID_TO_PROFILE_CONNECTION, testForMissingVppIdToProfileConnectionsUseCase().toString())
    }
}