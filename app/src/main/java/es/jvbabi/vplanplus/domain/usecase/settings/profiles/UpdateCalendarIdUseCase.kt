package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.calendar.UpdateCalendarUseCase

class UpdateCalendarIdUseCase(
    private val profileRepository: ProfileRepository,
    private val updateCalendarUseCase: UpdateCalendarUseCase
) {
    suspend operator fun invoke(profile: Profile, calendarId: Long?) {
        profileRepository.setCalendarIdForProfile(profile, calendarId)
        updateCalendarUseCase()
    }
}