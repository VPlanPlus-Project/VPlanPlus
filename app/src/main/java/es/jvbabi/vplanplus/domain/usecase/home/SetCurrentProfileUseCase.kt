package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class SetCurrentProfileUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profileId: UUID) {
        if (profileRepository.getProfiles().first().none { it.id == profileId }) return
        keyValueRepository.set(Keys.ACTIVE_PROFILE, profileId.toString())
    }
}