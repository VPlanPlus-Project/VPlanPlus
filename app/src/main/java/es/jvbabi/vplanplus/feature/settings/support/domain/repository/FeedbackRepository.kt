package es.jvbabi.vplanplus.feature.settings.support.domain.repository

interface FeedbackRepository {
    suspend fun sendFeedback(
        email: String?,
        feedback: String,
        attachSystemDetails: Boolean,
    ): Boolean
}