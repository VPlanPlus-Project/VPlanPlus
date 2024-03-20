package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class UpdateCalendarTypeUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(profile: Profile, calendarType: ProfileCalendarType) {
        profileRepository.updateProfile(
            (profileRepository.getDbProfileById(profileId = profile.id) ?: return)
                .copy(calendarMode = calendarType)
        )
    }
}