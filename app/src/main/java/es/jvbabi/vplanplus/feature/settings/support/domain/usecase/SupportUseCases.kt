package es.jvbabi.vplanplus.feature.settings.support.domain.usecase

data class SupportUseCases(
    val getEmailForSupportUseCase: GetEmailForSupportUseCase,
    val validateFeedbackUseCase: ValidateFeedbackUseCase,
    val validateEmailUseCase: ValidateEmailUseCase,
    val sendFeedbackUseCase: SendFeedbackUseCase
)