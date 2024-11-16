package es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.feature.settings.profile.notifications.ui.ProfileNotificationSettingsNotificationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class CanSendProfileNotificationsUseCase(
    private val systemRepository: SystemRepository
) {
    operator fun invoke(profile: Profile): Flow<ProfileNotificationSettingsNotificationState> {
        return combine(
            systemRepository.canSendNotifications(),
            systemRepository.canSendNotifications("PROFILE_${profile.id.toString().lowercase()}"),
        ) { canSendNotifications, canSendProfileNotifications ->
            if (!canSendNotifications) return@combine ProfileNotificationSettingsNotificationState.NOTIFICATIONS_DISABLED
            if (!canSendProfileNotifications) return@combine ProfileNotificationSettingsNotificationState.PROFILE_DISABLED
            return@combine ProfileNotificationSettingsNotificationState.ENABLED
        }.map { it }
    }
}