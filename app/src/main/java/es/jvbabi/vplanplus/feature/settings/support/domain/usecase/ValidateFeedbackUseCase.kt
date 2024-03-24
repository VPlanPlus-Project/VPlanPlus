package es.jvbabi.vplanplus.feature.settings.support.domain.usecase

class ValidateFeedbackUseCase {
    operator fun invoke(feedback: String): FeedbackError? {
        return when {
            feedback.isBlank() -> FeedbackError.EMPTY
            feedback.length < 10 -> FeedbackError.TOO_SHORT
            else -> null
        }
    }
}

enum class FeedbackError {
    EMPTY,
    TOO_SHORT
}