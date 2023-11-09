package es.jvbabi.vplanplus.ui.screens.settings.general

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneralSettingsViewModel @Inject constructor(
    keyValueUseCases: KeyValueUseCases
): ViewModel() {

    private val _state = mutableStateOf(GeneralSettingsState())
    val state: State<GeneralSettingsState> = _state

    init {
        viewModelScope.launch {
            keyValueUseCases.getFlow(Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE).collect {
                _state.value = _state.value.copy(
                    notificationShowNotificationIfAppIsVisible = it?.toBoolean() ?: false
                )
            }
        }
    }
}

data class GeneralSettingsState(
    val notificationShowNotificationIfAppIsVisible: Boolean = false
)