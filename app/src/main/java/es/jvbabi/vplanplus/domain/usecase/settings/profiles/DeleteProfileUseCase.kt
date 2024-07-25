package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.usecase.calendar.UpdateCalendarUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.UpdateFirebaseTokenUseCase
import kotlinx.coroutines.flow.first

class DeleteProfileUseCase(
    private val profileRepository: ProfileRepository,
    private val schoolRepository: SchoolRepository,
    private val keyValueRepository: KeyValueRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val notificationRepository: NotificationRepository,
    private val updateCalendarUseCase: UpdateCalendarUseCase,
    private val updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase
) {
    suspend operator fun invoke(profile: Profile): ProfileManagementDeletionResult {
        val currentProfile = getCurrentProfileUseCase().first() ?: return ProfileManagementDeletionResult.ERROR

        val updateFcm: suspend () -> Unit = {
            val firebaseToken = keyValueRepository.get(Keys.FCM_TOKEN)
            if (firebaseToken != null) updateFirebaseTokenUseCase(firebaseToken)
        }

        if (profile.id != currentProfile.id) {
            profileRepository.deleteProfile(profile)
            updateCalendarUseCase()
            updateFcm()
            return ProfileManagementDeletionResult.SUCCESS
        }

        val schoolProfiles = profileRepository.getProfilesBySchool(currentProfile.getSchool().id).first()
        if (schoolProfiles.size == 1 && schoolRepository.getSchools().size == 1) return ProfileManagementDeletionResult.LAST_PROFILE
        val newProfile = profileRepository.getProfiles().first().first { it.id != profile.id }
        keyValueRepository.set(Keys.ACTIVE_PROFILE, newProfile.id.toString())
        profileRepository.deleteProfile(profile)
        notificationRepository.deleteChannel("PROFILE_${profile.id.toString().lowercase()}")
        updateCalendarUseCase()
        updateFcm()
        return ProfileManagementDeletionResult.SUCCESS
    }
}

enum class ProfileManagementDeletionResult {
    SUCCESS,
    LAST_PROFILE,
    ERROR
}