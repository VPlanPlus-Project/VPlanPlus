package es.jvbabi.vplanplus.ui.screens.settings.general

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.home.Colors
import es.jvbabi.vplanplus.domain.usecase.settings.general.ColorScheme
import es.jvbabi.vplanplus.domain.usecase.settings.general.GeneralSettingsUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneralSettingsViewModel @Inject constructor(
    private val keyValueUseCases: KeyValueUseCases,
    private val generalSettingsUseCases: GeneralSettingsUseCases
) : ViewModel() {

    private val _state = mutableStateOf(GeneralSettingsState())
    val state: State<GeneralSettingsState> = _state

    init {
        viewModelScope.launch {
            combine(
                keyValueUseCases.getFlow(Keys.SETTINGS_SYNC_DAY_DIFFERENCE),
                keyValueUseCases.getFlow(Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE),
                keyValueUseCases.getFlow(Keys.COLOR)
            ) { syncDayDifference, showNotification, _ ->
                _state.value.copy(
                    syncDayDifference = syncDayDifference?.toInt() ?: Keys.SETTINGS_SYNC_DAY_DIFFERENCE_DEFAULT,
                    notificationShowNotificationIfAppIsVisible = showNotification?.toBoolean()
                        ?: false,
                    colors = generalSettingsUseCases.getColorsUseCase(_state.value.isDark ?: false),
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun init(isDark: Boolean) {
        _state.value = _state.value.copy(isDark = isDark)
    }

    fun onShowNotificationsOnAppOpenedClicked(show: Boolean) {
        viewModelScope.launch {
            keyValueUseCases.set(
                Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE,
                show.toString()
            )
        }
    }

    fun onColorSchemeChanged(color: Colors) {
        viewModelScope.launch {
            keyValueUseCases.set(Keys.COLOR, color.ordinal.toString())
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
    val colors: Map<Colors, ColorScheme> = mapOf(),
    val syncDayDifference: Int = 3,
    val isDark: Boolean? = null
)