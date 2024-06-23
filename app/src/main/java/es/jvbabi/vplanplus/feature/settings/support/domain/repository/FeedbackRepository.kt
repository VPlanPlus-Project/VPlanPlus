package es.jvbabi.vplanplus.feature.settings.support.domain.repository

import es.jvbabi.vplanplus.domain.model.Profile

interface FeedbackRepository {
    suspend fun sendFeedback(
        profile: Profile,
        email: String?,
        feedback: String,
        attachSystemDetails: Boolean,
    ): Boolean
}