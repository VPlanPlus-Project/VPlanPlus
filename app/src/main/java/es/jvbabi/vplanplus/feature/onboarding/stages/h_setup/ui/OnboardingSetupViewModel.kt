package es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.domain.usecase.OnboardingSetupUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingSetupViewModel @Inject constructor(
    private val onboardingSetupUseCases: OnboardingSetupUseCases
) : ViewModel() {
    var state by mutableStateOf(OnboardingSetupState())

    init {
        viewModelScope.launch {
            state = state.copy(isFirstProfile = onboardingSetupUseCases.isFirstProfileForSchoolUseCase())
            state = state.copy(isDone = true, hadError = !onboardingSetupUseCases.setupUseCase())
        }
    }
}

data class OnboardingSetupState(
    val isDone: Boolean = false,
    val hadError: Boolean = false,
    val isFirstProfile: Boolean = false
)