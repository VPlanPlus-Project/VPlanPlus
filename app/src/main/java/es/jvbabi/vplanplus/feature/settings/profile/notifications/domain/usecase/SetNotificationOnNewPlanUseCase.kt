package es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.CommonProfileNotificationSetting
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.RoomProfile
import es.jvbabi.vplanplus.domain.model.TeacherProfile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class SetNotificationOnNewPlanUseCase(
    private val profileRepository: ProfileRepository,
) {

    suspend operator fun invoke(profile: Profile, enabled: Boolean) {
        profileRepository.updateProfile(
            profile,
            notificationSettings = when (profile) {
                is ClassProfile -> profile.notificationSettings.copy(commonProfileNotificationSetting = (profile.notificationSettings as CommonProfileNotificationSetting).copy(newPlanEnabled = enabled))
                is TeacherProfile -> profile.notificationSettings.copy(commonProfileNotificationSetting = (profile.notificationSettings as CommonProfileNotificationSetting).copy(newPlanEnabled = enabled))
                is RoomProfile -> profile.notificationSettings.copy(commonProfileNotificationSetting = (profile.notificationSettings as CommonProfileNotificationSetting).copy(newPlanEnabled = enabled))
            }
        )
    }
}