package es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingDefaultLesson
import es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.domain.usecase.OnboardingDefaultLessonsUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingDefaultLessonsViewModel @Inject constructor(
    private val onboardingDefaultLessonsUseCases: OnboardingDefaultLessonsUseCases
) : ViewModel() {
    var state by mutableStateOf(OnboardingDefaultLessonsState())

    init {
        viewModelScope.launch {
            val defaultLessons = onboardingDefaultLessonsUseCases.getDefaultLessonsUseCase()
            state = state.copy(defaultLessons = defaultLessons.associateWith { true })
        }
    }

    fun doAction(action: UiAction) {
        when (action) {
            is ToggleDefaultLesson -> state = state.copy(defaultLessons = state.defaultLessons.toMutableMap().apply {
                this[action.defaultLesson] = this[action.defaultLesson]?.not() ?: true
            })
            is OnProceed -> {
                action.after()
            }
        }
    }
}

data class OnboardingDefaultLessonsState(
    val defaultLessons: Map<OnboardingDefaultLesson, Boolean> = emptyMap()
)

sealed class UiAction
data class ToggleDefaultLesson(val defaultLesson: OnboardingDefaultLesson) : UiAction()
data class OnProceed(val after: () -> Unit) : UiAction()