package es.jvbabi.vplanplus.feature.settings.advanced.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.settings.advanced.domain.data.FcmTokenReloadState
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.AdvancedSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.VppIdServer
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.servers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdvancedSettingsViewModel @Inject constructor(
    private val getClassByProfileUseCase: GetClassByProfileUseCase,
    private val getCurrentLessonNumberUseCase: GetCurrentLessonNumberUseCase,
    private val advancedSettingsUseCases: AdvancedSettingsUseCases,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
) : ViewModel() {

    private val _state = mutableStateOf(AdvancedSettingsState())
    val state: State<AdvancedSettingsState> = _state

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    getCurrentIdentityUseCase(),
                    advancedSettingsUseCases.getVppIdServerUseCase()
                )
            ) { data ->
                val currentIdentity = data[0] as Identity?
                val vppIdServer = data[1] as VppIdServer
                val canChangeVppIdServer = BuildConfig.DEBUG

                if (currentIdentity?.profile == null) return@combine AdvancedSettingsState()

                val currentLessonText = if (currentIdentity.profile.type == ProfileType.STUDENT) {
                    val `class` = getClassByProfileUseCase(currentIdentity.profile)!!
                    getCurrentLessonNumberUseCase(`class`).toString()
                } else {
                    "N/A"
                }

                _state.value.copy(
                    currentProfileText = """
                        Type: ${currentIdentity.profile.type}
                        Name: ${currentIdentity.profile.originalName} (${currentIdentity.profile.displayName})
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

    fun showDeleteCacheDialog() {
        _state.value = _state.value.copy(showDeleteCacheDialog = true)
    }

    fun deleteCache() {
        viewModelScope.launch {
            advancedSettingsUseCases.deleteCacheUseCase()
            closeDeleteCacheDialog()
        }
    }

    fun closeDeleteCacheDialog() {
        _state.value = _state.value.copy(showDeleteCacheDialog = false)
    }

    fun showVppIdDialog(show: Boolean) {
        _state.value = _state.value.copy(showVppIdServerDialog = show)
    }

    fun setVppIdServer(server: String?) {
        viewModelScope.launch {
            advancedSettingsUseCases.setVppIdServerUseCase(server)
            showVppIdDialog(false)
        }
    }

    fun onUpdateFcmToken() {
        if (state.value.fcmTokenReloadState == FcmTokenReloadState.LOADING) return
        viewModelScope.launch {
            _state.value = _state.value.copy(fcmTokenReloadState = FcmTokenReloadState.LOADING)
            if (advancedSettingsUseCases.updateFcmTokenUseCase())  _state.value = _state.value.copy(fcmTokenReloadState = FcmTokenReloadState.SUCCESS)
            else _state.value = _state.value.copy(fcmTokenReloadState = FcmTokenReloadState.ERROR)
        }
    }
}

data class AdvancedSettingsState(
    val currentProfileText: String = "Loading...",
    val currentLessonText: String = "Loading...",
    val showDeleteCacheDialog: Boolean = false,
    val showVppIdServerDialog: Boolean = false,
    val selectedVppIdServer: VppIdServer = servers.first(),
    val canChangeVppIdServer: Boolean = false,
    val fcmTokenReloadState: FcmTokenReloadState = FcmTokenReloadState.NONE
)