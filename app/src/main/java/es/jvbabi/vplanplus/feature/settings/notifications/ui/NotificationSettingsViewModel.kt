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
                    isDeveloperModeEnabledUseCase(),
                    notificationSettingsUseCases.isNotificationOnNewHomeworkEnabledUseCase(),
                    getCurrentServerUseCase()
                )
            ) { data ->
                val canSendNotifications = data[0] as Boolean
                val isDeveloperModeEnabled = data[1] as Boolean
                val isNotificationOnNewHomeworkEnabled = data[2] as Boolean
                val currentServer = data[3] as VppIdServer

                state.copy(
                    currentServer = currentServer,
                    canSendNotifications = canSendNotifications,
                    sendNotificationOnNewHomework = isNotificationOnNewHomeworkEnabled,

                    isDeveloperModeEnabled = isDeveloperModeEnabled
                )
            }.collect { state = it }
        }
    }
}


data class NotificationSettingsState(
    val currentServer: VppIdServer = servers.first(),

    val canSendNotifications: Boolean = true,

    val isDeveloperModeEnabled: Boolean = false,

    val sendNotificationOnNewHomework: Boolean = false
)