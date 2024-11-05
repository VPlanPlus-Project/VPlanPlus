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
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
@Suppress("UNCHECKED_CAST")
class NotificationSettingsViewModel @Inject constructor(
    private val notificationSettingsUseCases: NotificationSettingsUseCases,
    private val isDeveloperModeEnabledUseCase: IsDeveloperModeEnabledUseCase,
    private val getCurrentServerUseCase: GetVppIdServerUseCase
) : ViewModel() {

    var state by mutableStateOf<NotificationSettingsState?>(null)
        private set

    init {
        viewModelScope.launch {
            combine(
                listOf<Flow<Any>>(
                    notificationSettingsUseCases.isNotificationsEnabledUseCase(),
                    isDeveloperModeEnabledUseCase(),
                    getCurrentServerUseCase(),
                    notificationSettingsUseCases.isDailyReminderEnabledUseCase(),
                    notificationSettingsUseCases.getDailyReminderTimeUseCase()
                )
            ) { data ->
                val canSendNotifications = data[0] as Boolean
                val isDeveloperModeEnabled = data[1] as Boolean
                val currentServer = data[2] as VppIdServer
                val sendDailyReminder = data[3] as Boolean
                val dailyReminderTimes = data[4] as Map<DayOfWeek, LocalTime>

                (state ?: NotificationSettingsState()).copy(
                    currentServer = currentServer,
                    canSendNotifications = canSendNotifications,

                    isDeveloperModeEnabled = isDeveloperModeEnabled,

                    isDailyReminderEnabled = sendDailyReminder,
                    dailyReminderTimes = dailyReminderTimes
                )
            }.collect { state = it }
        }
    }

    fun onEvent(event: NotificationSettingsEvent) {
        val state = state ?: return
        viewModelScope.launch {
            when (event) {
                NotificationSettingsEvent.ToggleDailyReminder -> {
                    notificationSettingsUseCases.setDailyReminderEnabledUseCase(!state.isDailyReminderEnabled)
                }
                is NotificationSettingsEvent.SetDailyReminderTime -> {
                    notificationSettingsUseCases.setDailyReminderTimeUseCase(event.dayOfWeek.value, event.time)
                }
                is NotificationSettingsEvent.TriggerNotification -> {
                    notificationSettingsUseCases.sendNotificationUseCase()
                }
            }
        }
    }
}


data class NotificationSettingsState(
    val currentServer: VppIdServer = servers.first(),

    val canSendNotifications: Boolean = true,

    val isDeveloperModeEnabled: Boolean = false,

    val isDailyReminderEnabled: Boolean = false,
    val dailyReminderTimes: Map<DayOfWeek, LocalTime> = emptyMap(),
)

sealed class NotificationSettingsEvent {
    data object ToggleDailyReminder : NotificationSettingsEvent()
    data class SetDailyReminderTime(val dayOfWeek: DayOfWeek, val time: LocalTime) : NotificationSettingsEvent()
    data object TriggerNotification: NotificationSettingsEvent()
}