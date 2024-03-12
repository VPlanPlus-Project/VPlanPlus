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
                val vppEmail = data[0] as String?

                val sender =
                    if (state.value.sender == SupportMessageSender.VPP_ID_ANONYMOUS && !vppEmail.isNullOrBlank()) SupportMessageSender.VPP_ID_ANONYMOUS
                    else if (!vppEmail.isNullOrBlank()) SupportMessageSender.VPPID
                    else SupportMessageSender.ANONYMOUS

                state.value.copy(
                    sender = sender,
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

    fun toggleSender() {
        state.value = state.value.copy(
            sender = when (state.value.sender) {
                SupportMessageSender.VPP_ID_ANONYMOUS -> SupportMessageSender.VPPID
                SupportMessageSender.VPPID -> SupportMessageSender.VPP_ID_ANONYMOUS
                else -> SupportMessageSender.ANONYMOUS
            }
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
}

data class SupportScreenState(
    val feedback: String = "",
    val sender: SupportMessageSender = SupportMessageSender.ANONYMOUS,
    val email: String? = null,
    val emailValid: Boolean = true,
    val attachSystemDetails: Boolean = true,
    val isLoading: Boolean = false,
    val feedbackError: FeedbackError? = null,
)

enum class SupportMessageSender {
    VPP_ID_ANONYMOUS,
    VPPID,
    ANONYMOUS
}