package es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
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
            onboardingSetupUseCases.setupUseCase().let { profile ->
                state = state.copy(isDone = true, hadError = profile == null, profile = profile)
            }
            if (state.profile != null) onboardingSetupUseCases.getProfileByIdUseCase(state.profile!!.id).collect {
                state = state.copy(profile = it)
            }
        }
    }

    fun onEvent(event: OnboardingSetupEvent) {
        viewModelScope.launch {
            when (event) {
                is OnboardingSetupEvent.ToggleNotifications -> onboardingSetupUseCases.toggleNotificationForProfileUseCase(state.profile!!, event.enabled)
                is OnboardingSetupEvent.ToggleHomework -> onboardingSetupUseCases.updateHomeworkEnabledUseCase((state.profile as? ClassProfile) ?: return@launch, event.enabled)
                is OnboardingSetupEvent.ToggleAssessments -> onboardingSetupUseCases.updateAssessmentsEnabledUseCase((state.profile as? ClassProfile) ?: return@launch, event.enabled)
            }
        }
    }
}

data class OnboardingSetupState(
    val isDone: Boolean = false,
    val hadError: Boolean = false,
    val isFirstProfile: Boolean = false,
    val profile: Profile? = null,
)

sealed class OnboardingSetupEvent {
    data class ToggleNotifications(val enabled: Boolean) : OnboardingSetupEvent()
    data class ToggleHomework(val enabled: Boolean) : OnboardingSetupEvent()
    data class ToggleAssessments(val enabled: Boolean) : OnboardingSetupEvent()
}