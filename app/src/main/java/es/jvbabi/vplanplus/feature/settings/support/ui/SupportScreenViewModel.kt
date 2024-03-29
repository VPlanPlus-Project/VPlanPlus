package es.jvbabi.vplanplus.feature.settings.support.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.FeedbackError
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.SupportUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportScreenViewModel @Inject constructor(
    private val supportUseCases: SupportUseCases
) : ViewModel() {

    val state = mutableStateOf(SupportScreenState())

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    supportUseCases.getEmailForSupportUseCase()
                )
            ) { data ->
                val vppEmail = data[0]

                state.value.copy(
                    email = vppEmail
                )
            }.collect {
                state.value = it
            }
        }
    }

    fun onUpdateFeedback(feedback: String) {
        state.value = state.value.copy(
            feedback = feedback,
            feedbackError = supportUseCases.validateFeedbackUseCase(feedback)
        )
    }

    fun toggleAttachSystemDetails() {
        state.value = state.value.copy(attachSystemDetails = !state.value.attachSystemDetails)
    }

    fun onUpdateEmail(email: String) {
        state.value = state.value.copy(
            email = email,
            emailValid = supportUseCases.validateEmailUseCase(email)
        )
    }

    fun send() {
        if (state.value.isLoading) return
        if (!state.value.emailValid || state.value.feedbackError != null) return
        state.value = state.value.copy(isLoading = true)
        viewModelScope.launch {
            state.value = state.value.copy(
                sendState = if (supportUseCases.sendFeedbackUseCase(
                    state.value.email,
                    state.value.feedback,
                    state.value.attachSystemDetails
                )) FeedbackSendState.SUCCESS else FeedbackSendState.ERROR,
                isLoading = false
            )
        }
    }
}

data class SupportScreenState(
    val feedback: String = "",
    val email: String? = null,
    val emailValid: Boolean = true,
    val attachSystemDetails: Boolean = true,
    val isLoading: Boolean = false,
    val feedbackError: FeedbackError? = null,
    val sendState: FeedbackSendState = FeedbackSendState.NONE
)

enum class FeedbackSendState {
    NONE,
    SUCCESS,
    ERROR
}