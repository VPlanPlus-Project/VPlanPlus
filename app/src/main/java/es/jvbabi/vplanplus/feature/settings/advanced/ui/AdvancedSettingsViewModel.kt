package es.jvbabi.vplanplus.feature.settings.advanced.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.data.FcmTokenReloadState
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.AdvancedSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.VppIdServer
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.servers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdvancedSettingsViewModel @Inject constructor(
    private val getCurrentLessonNumberUseCase: GetCurrentLessonNumberUseCase,
    private val advancedSettingsUseCases: AdvancedSettingsUseCases,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) : ViewModel() {

    private val _state = mutableStateOf(AdvancedSettingsState())
    val state: State<AdvancedSettingsState> = _state

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    getCurrentProfileUseCase(),
                    advancedSettingsUseCases.getVppIdServerUseCase()
                )
            ) { data ->
                val currentProfile = data[0] as Profile
                val vppIdServer = data[1] as VppIdServer
                val canChangeVppIdServer = BuildConfig.DEBUG


                val currentLessonText = if (currentProfile is ClassProfile) {
                    getCurrentLessonNumberUseCase(currentProfile.group).toString()
                } else {
                    "N/A"
                }

                _state.value.copy(
                    currentProfileText = """
                        Type: ${currentProfile.getType()}
                        Name: ${currentProfile.originalName} (${currentProfile.displayName})
                    """.trimIndent(),
                    selectedVppIdServer = vppIdServer,
                    currentLessonText = currentLessonText,
                    canChangeVppIdServer = canChangeVppIdServer
                )
            }.collect {
                _state.value = it
            }
        }
    }

    private fun deleteCache() {
        viewModelScope.launch {
            advancedSettingsUseCases.deleteCacheUseCase()
        }
    }

    private fun setVppIdServer(server: String?) {
        viewModelScope.launch {
            advancedSettingsUseCases.setVppIdServerUseCase(server)
        }
    }

    private fun onUpdateFcmToken() {
        if (state.value.fcmTokenReloadState == FcmTokenReloadState.LOADING) return
        viewModelScope.launch {
            _state.value = _state.value.copy(fcmTokenReloadState = FcmTokenReloadState.LOADING)
            if (advancedSettingsUseCases.updateFcmTokenUseCase())  _state.value = _state.value.copy(fcmTokenReloadState = FcmTokenReloadState.SUCCESS)
            else _state.value = _state.value.copy(fcmTokenReloadState = FcmTokenReloadState.ERROR)
        }
    }

    fun onEvent(event: AdvancedSettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is AdvancedSettingsEvent.DeleteCache -> deleteCache()
                is AdvancedSettingsEvent.SetVppIdServer -> setVppIdServer(event.server)
                is AdvancedSettingsEvent.UpdateFcmToken -> onUpdateFcmToken()
                is AdvancedSettingsEvent.ResetBalloons -> advancedSettingsUseCases.resetBalloonsUseCase()
                is AdvancedSettingsEvent.TriggerHomeworkReminder -> advancedSettingsUseCases.homeworkReminderUseCase()
            }
        }
    }
}

data class AdvancedSettingsState(
    val currentProfileText: String = "Loading...",
    val currentLessonText: String = "Loading...",
    val selectedVppIdServer: VppIdServer = servers.first(),
    val canChangeVppIdServer: Boolean = false,
    val fcmTokenReloadState: FcmTokenReloadState = FcmTokenReloadState.NONE
)

sealed class AdvancedSettingsEvent {
    data object DeleteCache : AdvancedSettingsEvent()
    data class SetVppIdServer(val server: String?) : AdvancedSettingsEvent()
    data object UpdateFcmToken : AdvancedSettingsEvent()
    data object ResetBalloons : AdvancedSettingsEvent()

    data object TriggerHomeworkReminder: AdvancedSettingsEvent()
}