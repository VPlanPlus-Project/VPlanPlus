package es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.OnboardingProfileTypeUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingProfileTypeViewModel @Inject constructor(
    private val onboardingProfileTypeUseCases: OnboardingProfileTypeUseCases
) : ViewModel() {
    var state by mutableStateOf(OnboardingProfileTypeState())

    init {
        viewModelScope.launch {
            state = state.copy(isFirstProfileForSchool = onboardingProfileTypeUseCases.isFirstProfileForSchoolUseCase())
        }
    }

    fun doAction(action: UiAction) {
        when (action) {
            is SelectProfileType -> state = state.copy(profileType = action.profileType)
            is OnProceed -> {
                viewModelScope.launch {
                    onboardingProfileTypeUseCases.setProfileTypeUseCase(state.profileType!!)
                    action.after()
                }
            }
        }
    }
}

data class OnboardingProfileTypeState(
    val isLimitedToStudentProfile: Boolean = false,
    val isFirstProfileForSchool: Boolean = false,
    val profileType: ProfileType? = null
)

sealed class UiAction
data class SelectProfileType(val profileType: ProfileType) : UiAction()
data class OnProceed(val after: () -> Unit) : UiAction()