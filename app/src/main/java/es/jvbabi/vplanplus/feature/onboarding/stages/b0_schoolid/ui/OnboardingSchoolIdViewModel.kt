package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase.OnboardingSchoolIdUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingSchoolIdViewModel @Inject constructor(
    private val onboardingSchoolIdUseCases: OnboardingSchoolIdUseCases
) : ViewModel() {
    var state by mutableStateOf(OnboardingSchoolIdState())

    private var checkSchoolIdJob: Job? = null

    fun doAction(action: UiAction) {
        viewModelScope.launch {
            when (action) {
                is InputSchoolId -> {
                    if (action.schoolId == "") {
                        state = state.copy(sp24SchoolId = "", schoolIdError = null)
                        return@launch
                    }
                    val schoolId = action.schoolId.toIntOrNull() ?: return@launch
                    state = state.copy(isLoading = false, schoolIdError = null, sp24SchoolId = schoolId.toString())
                    checkSchoolIdJob?.cancel()
                    if (onboardingSchoolIdUseCases.isSchoolIdValidUseCase(schoolId)) {
                        state = state.copy(isLoading = true)
                        checkSchoolIdJob = viewModelScope.launch {
                            state = state.copy(isLoading = false, schoolIdError = onboardingSchoolIdUseCases.doesSchoolIdExistsUseCase(schoolId))
                        }
                    }
                }
                is OnProceed -> {
                    if (state.canProceed) {
                        onboardingSchoolIdUseCases.setSchoolIdUseCase(state.sp24SchoolId.toInt())
                        action.onSet()
                    }
                }
            }
        }
    }
}

data class OnboardingSchoolIdState(
    val sp24SchoolId: String = "",
    val isLoading: Boolean = false,
    val schoolIdError: SchoolIdError? = null
) {
    val canProceed: Boolean
        get() = schoolIdError == null && !isLoading && sp24SchoolId.length == 8 && sp24SchoolId.toIntOrNull() != null
}

enum class SchoolIdError {
    DOES_NOT_EXIST,
    NETWORK_ERROR
}

sealed class UiAction
data class InputSchoolId(val schoolId: String) : UiAction()
data class OnProceed(val onSet: () -> Unit) : UiAction()