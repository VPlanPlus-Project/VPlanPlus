package es.jvbabi.vplanplus.feature.settings.notifications.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.general.IsDeveloperModeEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.NotificationSettingsUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val notificationSettingsUseCases: NotificationSettingsUseCases,
    private val isDeveloperModeEnabledUseCase: IsDeveloperModeEnabledUseCase
): ViewModel() {

    var state by mutableStateOf(NotificationSettingsState())
        private set

    init {
        viewModelScope.launch {
            combine(
                listOf<Flow<Any>>(
                    notificationSettingsUseCases.isNotificationsEnabledUseCase(),
                    notificationSettingsUseCases.isAutomaticReminderTimeEnabledUseCase(),
                    isDeveloperModeEnabledUseCase()
                )
            ) { data ->
                val canSendNotifications = data[0] as Boolean
                val isAutomaticReminderTimeEnabled = data[1] as Boolean
                val isDeveloperModeEnabled = data[2] as Boolean

                state.copy(
                    canSendNotifications = canSendNotifications,
                    isAutomaticReminderTimeEnabled = isAutomaticReminderTimeEnabled,

                    isDeveloperModeEnabled = isDeveloperModeEnabled
                )
            }.collect { state = it }
        }
    }

    fun doAction(event: NotificationSettingsEvent) {
        when (event) {
            is NotificationSettingsEvent.ToggleAutomaticReminderTime -> {
                viewModelScope.launch {
                    notificationSettingsUseCases.setAutomaticReminderTimeEnabledUseCase(!state.isAutomaticReminderTimeEnabled)
                }
            }
            is NotificationSettingsEvent.UpdateAutomaticTimes -> {
                viewModelScope.launch {
                    notificationSettingsUseCases.developerUpdateDynamicTimesUseCase()
                }
            }
            is NotificationSettingsEvent.TriggerNdpReminderNotification -> {
                viewModelScope.launch {
                    notificationSettingsUseCases.developerTriggerNdpReminderNotificationUseCase()
                }
            }
        }
    }
}

data class NotificationSettingsState(
    val canSendNotifications: Boolean = true,
    val isAutomaticReminderTimeEnabled: Boolean = true,

    val isDeveloperModeEnabled: Boolean = false
)

sealed class NotificationSettingsEvent {
    data object ToggleAutomaticReminderTime: NotificationSettingsEvent()

    data object UpdateAutomaticTimes: NotificationSettingsEvent()
    data object TriggerNdpReminderNotification: NotificationSettingsEvent()
}