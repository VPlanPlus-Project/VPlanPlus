package es.jvbabi.vplanplus.domain.usecase.settings.profiles.shared

import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import java.util.UUID

class GetProfileByIdUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(profileId: UUID) = profileRepository.getProfileById(profileId)
}