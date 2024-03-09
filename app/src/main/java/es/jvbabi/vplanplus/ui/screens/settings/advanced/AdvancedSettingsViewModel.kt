package es.jvbabi.vplanplus.ui.screens.settings.advanced

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.advanced.AdvancedSettingsUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdvancedSettingsViewModel @Inject constructor(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val getClassByProfileUseCase: GetClassByProfileUseCase,
    private val getCurrentLessonNumberUseCase: GetCurrentLessonNumberUseCase,
    private val advancedSettingsUseCases: AdvancedSettingsUseCases
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
                val currentProfile = data[0] as Profile?
                val vppIdServer = data[1] as String
                val canChangeVppIdServer = BuildConfig.DEBUG

                if (currentProfile == null) return@combine AdvancedSettingsState()

                val currentLessonText = if (currentProfile.type == ProfileType.STUDENT) {
                    val `class` = getClassByProfileUseCase(currentProfile)!!
                    getCurrentLessonNumberUseCase(`class`).toString()
                } else {
                    "N/A"
                }

                _state.value.copy(
                    currentProfileText = """
                        Type: ${currentProfile.type}
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
}

data class AdvancedSettingsState(
    val currentProfileText: String = "Loading...",
    val currentLessonText: String = "Loading...",
    val showDeleteCacheDialog: Boolean = false,
    val showVppIdServerDialog: Boolean = false,
    val selectedVppIdServer: String = "",
    val canChangeVppIdServer: Boolean = false
)