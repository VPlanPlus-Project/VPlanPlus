package es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.domain.usecase.OnboardingProfileSelectUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingProfileSelectViewModel @Inject constructor(
    private val onboardingProfileSelectUseCases: OnboardingProfileSelectUseCases,
) : ViewModel() {
    var state by mutableStateOf(OnboardingProfileSelectState())

    init {
        viewModelScope.launch {
            val options = onboardingProfileSelectUseCases.getProfileOptionsUseCase()
            val isFirstProfile = onboardingProfileSelectUseCases.isFirstProfileForSchoolUseCase()
            state = state.copy(
                options = options.options,
                profileType = options.profileType,
                isFirstProfile = isFirstProfile
            )
        }
    }

    fun doAction(action: UiAction) {
        when (action) {
            is SelectOption -> state = state.copy(selectedOption = action.option)
            is OnProceed -> {
                viewModelScope.launch {
                    onboardingProfileSelectUseCases.setProfileUseCase(state.selectedOption!!)
                    action.after()
                }
            }
        }
    }
}

data class OnboardingProfileSelectState(
    val isFirstProfile: Boolean = false,
    val profileType: ProfileType = ProfileType.STUDENT,
    val options: Map<String, Int?> = emptyMap(),
    val selectedOption: String? = null
) {
    val canProceed: Boolean
        get() = selectedOption != null && options.keys.contains(selectedOption)
}

sealed class UiAction
data class SelectOption(val option: String) : UiAction()
data class OnProceed(val after: () -> Unit) : UiAction()