package es.jvbabi.vplanplus.feature.settings.general.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.home.Colors
import es.jvbabi.vplanplus.domain.usecase.settings.general.GeneralSettings
import es.jvbabi.vplanplus.domain.usecase.settings.general.GeneralSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.general.domain.data.AppThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneralSettingsViewModel @Inject constructor(
    private val generalSettingsUseCases: GeneralSettingsUseCases
) : ViewModel() {

    private val _state = mutableStateOf(GeneralSettingsState())
    val state: State<GeneralSettingsState> = _state

    private var uiJob: Job? = null

    fun init(isDark: Boolean) {
        uiJob?.cancel()
        uiJob = viewModelScope.launch {
            generalSettingsUseCases.getSettingsUseCase(isDark).collect {
                _state.value = _state.value.copy(settings = it)
            }
        }
    }

    fun onShowNotificationsOnAppOpenedClicked(show: Boolean) {
        viewModelScope.launch {
            generalSettingsUseCases.updateSettingsUseCase(
                _state.value.settings!!.copy(
                    showNotificationsIfAppIsVisible = show
                )
            )
        }
    }

    fun onColorSchemeChanged(color: Colors) {
        viewModelScope.launch {
            generalSettingsUseCases.updateSettingsUseCase(
                _state.value.settings!!.copy(
                    colorScheme = _state.value.settings!!.colorScheme.map { it.key to it.value.copy(active = it.key.ordinal == color.ordinal) }.toMap()
                )
            )
        }
    }

    fun onAppThemeModeChanged(appThemeMode: AppThemeMode) {
        viewModelScope.launch {
            generalSettingsUseCases.updateSettingsUseCase(
                _state.value.settings!!.copy(appThemeMode = appThemeMode)
            )
        }
    }

    fun onSyncDaysAheadSet(days: Int) {
        viewModelScope.launch {
            generalSettingsUseCases.updateSettingsUseCase(
                _state.value.settings!!.copy(daysAheadSync = days)
            )
        }
    }

    fun onToggleGradeProtection(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            generalSettingsUseCases.updateGradeProtectionUseCase(!_state.value.settings!!.isBiometricEnabled, fragmentActivity)
        }
    }

    fun onHideFinishedLessonsChanged(hide: Boolean) {
        viewModelScope.launch {
            generalSettingsUseCases.updateSettingsUseCase(
                _state.value.settings!!.copy(hideFinishedLessons = hide)
            )
        }
    }

    fun onSyncIntervalChanged(minutes: Int) {
        viewModelScope.launch {
            generalSettingsUseCases.updateSettingsUseCase(
                _state.value.settings!!.copy(syncIntervalMinutes = minutes)
            )
        }
    }
}

data class GeneralSettingsState(
    val settings: GeneralSettings? = null,
)