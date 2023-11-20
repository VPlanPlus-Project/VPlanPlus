package es.jvbabi.vplanplus.ui.screens.settings.general

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneralSettingsViewModel @Inject constructor(
    private val keyValueUseCases: KeyValueUseCases
): ViewModel() {

    private val _state = mutableStateOf(GeneralSettingsState())
    val state: State<GeneralSettingsState> = _state

    init {
        viewModelScope.launch {
            combine(
                keyValueUseCases.getFlow(Keys.SETTINGS_SYNC_DAY_DIFFERENCE),
                keyValueUseCases.getFlow(Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE)
            ) { syncDayDifference, showNotification ->
                _state.value.copy(
                    syncDayDifference = syncDayDifference?.toInt() ?: 3,
                    notificationShowNotificationIfAppIsVisible = showNotification?.toBoolean() ?: false
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun onShowNotificationsOnAppOpenedClicked(show: Boolean) {
        viewModelScope.launch {
            keyValueUseCases.set(Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE, show.toString())
        }
    }

    fun onSyncDaysAheadSet(days: Int) {
        viewModelScope.launch {
            keyValueUseCases.set(Keys.SETTINGS_SYNC_DAY_DIFFERENCE, days.toString())
        }
    }
}

data class GeneralSettingsState(
    val notificationShowNotificationIfAppIsVisible: Boolean = false,

    val syncDayDifference: Int = 3
)