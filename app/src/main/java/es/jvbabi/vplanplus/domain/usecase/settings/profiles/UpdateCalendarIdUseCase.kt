package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class UpdateCalendarIdUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profile, calendarId: Long?) {
        profileRepository.updateProfile(
            profileRepository.getDbProfileById(profileId = profile.id)!!.copy(calendarId = calendarId)
        )
    }
}