package es.jvbabi.vplanplus.feature.settings.notifications.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsDeveloperModeEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.VppIdServer
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.servers
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.NotificationSettingsUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val notificationSettingsUseCases: NotificationSettingsUseCases,
    private val isDeveloperModeEnabledUseCase: IsDeveloperModeEnabledUseCase,
    private val getCurrentServerUseCase: GetVppIdServerUseCase
): ViewModel() {

    var state by mutableStateOf(NotificationSettingsState())
        private set

    init {
        viewModelScope.launch {
            combine(
                listOf<Flow<Any>>(
                    notificationSettingsUseCases.isNotificationsEnabledUseCase(),
                    notificationSettingsUseCases.isAutomaticReminderTimeEnabledUseCase(),
                    isDeveloperModeEnabledUseCase(),
                    notificationSettingsUseCases.isNotificationOnNewHomeworkEnabledUseCase(),
                    getCurrentServerUseCase()
                )
            ) { data ->
                val canSendNotifications = data[0] as Boolean
                val isAutomaticReminderTimeEnabled = data[1] as Boolean
                val isDeveloperModeEnabled = data[2] as Boolean
                val isNotificationOnNewHomeworkEnabled = data[3] as Boolean
                val currentServer = data[4] as VppIdServer

                state.copy(
                    currentServer = currentServer,
                    canSendNotifications = canSendNotifications,
                    isAutomaticReminderTimeEnabled = isAutomaticReminderTimeEnabled,
                    sendNotificationOnNewHomework = isNotificationOnNewHomeworkEnabled,

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
            is NotificationSettingsEvent.ToggleNewHomeworkNotification -> {
                viewModelScope.launch {
                    notificationSettingsUseCases.toggleNotificationOnNewHomeworkUseCase()
                }
            }
        }
    }
}

data class NotificationSettingsState(
    val currentServer: VppIdServer = servers.first(),

    val canSendNotifications: Boolean = true,
    val isAutomaticReminderTimeEnabled: Boolean = true,

    val isDeveloperModeEnabled: Boolean = false,

    val sendNotificationOnNewHomework: Boolean = false
)

sealed class NotificationSettingsEvent {
    data object ToggleAutomaticReminderTime: NotificationSettingsEvent()

    data object UpdateAutomaticTimes: NotificationSettingsEvent()
    data object TriggerNdpReminderNotification: NotificationSettingsEvent()

    data object ToggleNewHomeworkNotification: NotificationSettingsEvent()
}