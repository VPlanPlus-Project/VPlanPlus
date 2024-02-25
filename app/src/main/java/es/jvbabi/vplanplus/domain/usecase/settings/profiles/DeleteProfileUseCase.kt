package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import kotlinx.coroutines.flow.first

class DeleteProfileUseCase(
    private val profileRepository: ProfileRepository,
    private val schoolRepository: SchoolRepository,
    private val keyValueRepository: KeyValueRepository,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(profile: Profile): ProfileManagementDeletionResult {
        val currentIdentity =
            getCurrentIdentityUseCase().first() ?: return ProfileManagementDeletionResult.ERROR

        if (profile.id != currentIdentity.profile?.id) {
            profileRepository.deleteProfile(profile.id)
            return ProfileManagementDeletionResult.SUCCESS
        }

        val schoolProfiles = profileRepository.getProfilesBySchoolId(
            (currentIdentity.school ?: return ProfileManagementDeletionResult.ERROR).schoolId
        )
        if (schoolProfiles.size == 1 && schoolRepository.getSchools().size == 1) return ProfileManagementDeletionResult.LAST_PROFILE
        val newProfile = profileRepository.getProfiles().first().first { it.id != profile.id }
        keyValueRepository.set(Keys.ACTIVE_PROFILE, newProfile.id.toString())
        profileRepository.deleteProfile(profile.id)
        notificationRepository.deleteChannel("PROFILE_${profile.id.toString().lowercase()}")
        return ProfileManagementDeletionResult.SUCCESS
    }
}

enum class ProfileManagementDeletionResult {
    SUCCESS,
    LAST_PROFILE,
    ERROR
}