package es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingCredentialsUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingLoginViewModel @Inject constructor(
    private val onboardingCredentialsUseCases: OnboardingCredentialsUseCases
) : ViewModel() {
    var state by mutableStateOf(OnboardingLoginState())

    init {
        viewModelScope.launch {
            val sp24SchoolId = onboardingCredentialsUseCases.getSp24SchoolIdUseCase()
            state = state.copy(sp24SchoolId = sp24SchoolId)
        }
    }

    fun doAction(action: UiAction) {
        when (action) {
            is InputUsername -> {
                state = state.copy(username = action.username)
                if (state.error == LoginError.BAD_CREDENTIALS) state = state.copy(error = null)
            }
            is InputPassword -> {
                state = state.copy(password = action.password)
                if (state.error == LoginError.BAD_CREDENTIALS) state = state.copy(error = null)
            }
            is OnProceed -> {
                state = state.copy(isLoading = true)
                viewModelScope.launch {
                    val onboardingInit = onboardingCredentialsUseCases.checkCredentialsAndInitOnboardingForSchoolUseCase(state.sp24SchoolId, state.username, state.password)
                    if (onboardingInit == null) {
                        state = state.copy(error = LoginError.NETWORK_ERROR, isLoading = false)
                        return@launch
                    }
                    if (!onboardingInit.areCredentialsCorrect) {
                        state = state.copy(error = LoginError.BAD_CREDENTIALS, isLoading = false)
                        return@launch
                    } else {
                        action.after()
                    }
                    state = state.copy(isLoading = false)
                }
            }
        }
    }
}

data class OnboardingLoginState(
    val sp24SchoolId: Int = 0,
    val username: String = "schueler",
    val password: String = "",
    val error: LoginError? = null,
    val isLoading: Boolean = false,
) {
    val canProceed: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading && (error == null || error == LoginError.NETWORK_ERROR)
}

enum class LoginError {
    BAD_CREDENTIALS,
    NETWORK_ERROR
}

sealed class UiAction

data class InputUsername(val username: String) : UiAction()
data class InputPassword(val password: String) : UiAction()
data class OnProceed(val after: () -> Unit) : UiAction()