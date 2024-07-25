package es.jvbabi.vplanplus.feature.settings.support.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase

data class SupportUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val validateFeedbackUseCase: ValidateFeedbackUseCase,
    val validateEmailUseCase: ValidateEmailUseCase,
    val sendFeedbackUseCase: SendFeedbackUseCase
)