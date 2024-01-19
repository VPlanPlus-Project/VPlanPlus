package es.jvbabi.vplanplus.ui.screens.settings.advanced

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.advanced.AdvancedSettingsUseCases
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
            getCurrentProfileUseCase().collect { currentProfile ->
                if (currentProfile == null) return@collect
                _state.value = _state.value.copy(currentProfileText = """
                    Type: ${currentProfile.type}
                    Name: ${currentProfile.originalName} (${currentProfile.displayName})
                """.trimIndent())

                if (currentProfile.type == ProfileType.STUDENT) {
                    val `class` = getClassByProfileUseCase(currentProfile)!!
                    _state.value = _state.value.copy(currentLessonText = getCurrentLessonNumberUseCase(`class`).toString())
                } else {
                    _state.value = _state.value.copy(currentLessonText = "N/A")
                }
            }
        }
    }

    fun showDeletePlanDataDialog() {
        _state.value = _state.value.copy(showDeletePlanData = true)
    }

    fun deletePlanData() {
        viewModelScope.launch {
            advancedSettingsUseCases.deletePlansUseCase()
            closeDeletePlanDataDialog()
        }
    }

    fun closeDeletePlanDataDialog() {
        _state.value = _state.value.copy(showDeletePlanData = false)
    }
}

data class AdvancedSettingsState(
    val currentProfileText: String = "Loading...",
    val currentLessonText: String = "Loading...",
    val showDeletePlanData: Boolean = false
)