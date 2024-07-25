package es.jvbabi.vplanplus.feature.onboarding.stages.a_welcome.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.onboarding.stages.a_welcome.domain.usecase.OnboardingWelcomeUseCases
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.VppIdServer
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.servers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingWelcomeViewModel @Inject constructor(
    private val onboardingWelcomeUseCases: OnboardingWelcomeUseCases
) : ViewModel() {
    var state by mutableStateOf(WelcomeState())

    init {
        viewModelScope.launch {
            onboardingWelcomeUseCases.getVppIdServerUseCase().collect {
                state = state.copy(server = it)
            }
        }
    }
}

data class WelcomeState(
    val server: VppIdServer = servers.first()
)