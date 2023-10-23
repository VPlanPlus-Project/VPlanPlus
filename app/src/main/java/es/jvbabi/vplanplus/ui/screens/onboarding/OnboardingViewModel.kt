package es.jvbabi.vplanplus.ui.screens.onboarding

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.Response
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

    fun newScreen() {
        _state.value = _state.value.copy(isLoading = false, currentErrorType = ErrorType.NONE)
    }

    suspend fun onSchoolIdSubmit() {
        _state.value = _state.value.copy(isLoading = true)
        schoolUseCases.checkSchoolIdOnline(state.value.schoolId).onEach { result ->
            Log.d("OnboardingViewModel", "onSchoolIdSubmit: $result")
            _state.value = _state.value.copy(
                isLoading = false,
                schoolIdState = result,
                currentErrorType = when (result) {
                    SchoolIdCheckResult.VALID -> ErrorType.NONE
                    SchoolIdCheckResult.NOT_FOUND -> ErrorType.NOT_FOUND
                    null -> ErrorType.NO_INTERNET
                    else -> ErrorType.OTHER
                }
            )
        }.launchIn(viewModelScope)
    }

    fun onUsernameInput(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun onPasswordInput(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onPasswordVisibilityToggle() {
        _state.value = _state.value.copy(passwordVisible = !state.value.passwordVisible)
    }

    suspend fun onLogin() {
        _state.value = _state.value.copy(isLoading = true)
        _state.value = _state.value.copy(
            isLoading = false, currentResponseType = schoolUseCases.login(
                state.value.schoolId,
                state.value.username,
                state.value.password
            )
        )
    }
}

data class OnboardingState(
    val schoolId: String = "",
    val schoolIdState: SchoolIdCheckResult? = SchoolIdCheckResult.INVALID,

    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val loginSuccessful: Boolean = false,

    @Deprecated("Use currentResponseType") val currentErrorType: ErrorType = ErrorType.NONE,
    val currentResponseType: Response = Response.NONE,
    val isLoading: Boolean = false,
)