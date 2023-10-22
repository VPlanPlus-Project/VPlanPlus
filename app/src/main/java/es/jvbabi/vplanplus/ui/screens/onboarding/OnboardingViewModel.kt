package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.util.ErrorType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val schoolUseCases: SchoolUseCases
) : ViewModel() {
    private val _state = mutableStateOf(OnboardingState())
    val state: State<OnboardingState> = _state

    fun onSchoolIdInput(schoolId: String) {
        _state.value = _state.value.copy(
            schoolId = schoolId,
            schoolIdState = schoolUseCases.checkSchoolId(schoolId)
        )
    }

    suspend fun onSchoolIdSubmit() {
        _state.value = _state.value.copy(isLoading = true)
        schoolUseCases.checkSchoolIdOnline(state.value.schoolId).onEach { result ->
            _state.value = _state.value.copy(
                isLoading = false,
                schoolIdState = result,
                currentErrorType = if (result == SchoolIdCheckResult.NOT_FOUND) ErrorType.NOT_FOUND else ErrorType.NONE
            )
        }.launchIn(viewModelScope)
    }

}

data class OnboardingState(
    val schoolId: String = "",
    val schoolIdState: SchoolIdCheckResult = SchoolIdCheckResult.INVALID,
    val currentErrorType: ErrorType = ErrorType.NONE,
    val isLoading: Boolean = false
)