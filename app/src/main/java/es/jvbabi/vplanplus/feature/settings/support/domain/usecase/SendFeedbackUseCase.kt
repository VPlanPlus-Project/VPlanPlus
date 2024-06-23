package es.jvbabi.vplanplus.feature.settings.support.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.settings.support.domain.repository.FeedbackRepository

class SendFeedbackUseCase(
    private val feedbackRepository: FeedbackRepository
) {
    suspend operator fun invoke(
        email: String?,
        profile: Profile,
        feedback: String,
        attachSystemDetails: Boolean
    ): Boolean {
        return feedbackRepository.sendFeedback(profile, email, feedback, attachSystemDetails)
    }
}