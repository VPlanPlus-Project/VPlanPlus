package es.jvbabi.vplanplus.feature.settings.profile.notifications.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.ProfileNotificationSettingsUseCases
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileNotificationSettingsViewModel @Inject constructor(
    private val profileNotificationSettingsUseCases: ProfileNotificationSettingsUseCases
) : ViewModel() {

    var state by mutableStateOf<ProfileNotificationSettingsState?>(null)
        private set

    @Suppress("UNCHECKED_CAST")
    fun init(profileId: UUID) {
        viewModelScope.launch {
            profileNotificationSettingsUseCases.getProfileByIdUseCase(profileId).collectLatest { profile ->
                if (profile == null) return@collectLatest
                combine(
                    listOf(
                        profileNotificationSettingsUseCases.canSendProfileNotificationsUseCase(profile),
                        profileNotificationSettingsUseCases.isDeveloperModeEnabledUseCase(),
                        profileNotificationSettingsUseCases.getDailyReminderTimeUseCase(profile)
                    )
                ) { data ->
                    val canSendProfileNotifications = data[0] as ProfileNotificationSettingsNotificationState
                    val isDeveloperModeEnabled = data[1] as Boolean
                    val dailyReminderTimesForProfile = data[2] as Map<DayOfWeek, LocalTime>

                    state?.copy(
                        profile = profile,
                        isDeveloperModeEnabled = isDeveloperModeEnabled,
                        notificationState = canSendProfileNotifications,
                        dailyReminderTimesForProfile = dailyReminderTimesForProfile
                    ) ?: ProfileNotificationSettingsState(
                        profile = profile,
                        isDeveloperModeEnabled = isDeveloperModeEnabled,
                        notificationState = canSendProfileNotifications,
                        dailyReminderTimesForProfile = dailyReminderTimesForProfile
                    )
                }.collect {
                    state = it
                }
            }
        }
    }

    fun onEvent(event: ProfileNotificationSettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfileNotificationSettingsEvent.ToggleNotificationForProfile -> profileNotificationSettingsUseCases.toggleNotificationForProfileUseCase((state?.profile as? ClassProfile) ?: return@launch, event.enabled)
                is ProfileNotificationSettingsEvent.ToggleNotificationOnNewAssessment -> profileNotificationSettingsUseCases.setNotificationOnNewAssessmentUseCase((state?.profile as? ClassProfile) ?: return@launch, event.enabled)
                is ProfileNotificationSettingsEvent.ToggleNotificationOnNewHomework -> profileNotificationSettingsUseCases.toggleSendNotificationOnNewHomeworkUseCase(event.enabled)
                is ProfileNotificationSettingsEvent.ToggleNotificationOnNewPlan -> profileNotificationSettingsUseCases.setNotificationOnNewPlanUseCase((state?.profile as? ClassProfile) ?: return@launch, event.enabled)
                is ProfileNotificationSettingsEvent.ToggleDailyReminder -> profileNotificationSettingsUseCases.setDailyReminderEnabledUseCase((state?.profile as? ClassProfile) ?: return@launch, event.enabled)
                is ProfileNotificationSettingsEvent.SetDailyReminderTime -> profileNotificationSettingsUseCases.setDailyReminderTimeUseCase((state?.profile as? ClassProfile) ?: return@launch, event.dayOfWeek, event.time)
                is ProfileNotificationSettingsEvent.TriggerNotification -> profileNotificationSettingsUseCases.sendNotificationUseCase((state?.profile as? ClassProfile) ?: return@launch, 0)
            }
        }
    }
}

data class ProfileNotificationSettingsState(
    val profile: Profile,
    val isDeveloperModeEnabled: Boolean = false,

    val notificationState: ProfileNotificationSettingsNotificationState,

    val dailyReminderTimesForProfile: Map<DayOfWeek, LocalTime> = emptyMap(),
)

enum class ProfileNotificationSettingsNotificationState {
    NOTIFICATIONS_DISABLED,
    PROFILE_DISABLED,
    ENABLED
}

sealed class ProfileNotificationSettingsEvent {
    data class ToggleNotificationForProfile(val enabled: Boolean) : ProfileNotificationSettingsEvent()

    data class ToggleNotificationOnNewAssessment(val enabled: Boolean) : ProfileNotificationSettingsEvent()
    data class ToggleNotificationOnNewHomework(val enabled: Boolean) : ProfileNotificationSettingsEvent()

    data class ToggleNotificationOnNewPlan(val enabled: Boolean) : ProfileNotificationSettingsEvent()

    data class ToggleDailyReminder(val enabled: Boolean) : ProfileNotificationSettingsEvent()
    data class SetDailyReminderTime(val dayOfWeek: DayOfWeek, val time: LocalTime) : ProfileNotificationSettingsEvent()
    data object TriggerNotification: ProfileNotificationSettingsEvent()
}