package es.jvbabi.vplanplus.feature.settings.support.data.repository

import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.settings.support.domain.repository.FeedbackRepository
import kotlinx.coroutines.flow.first

class FeedbackRepositoryImpl(
    private val vppIdRepository: VppIdRepository
) : FeedbackRepository {
    override suspend fun sendFeedback(
        vppId: Boolean,
        email: String?,
        feedback: String,
        attachSystemDetails: Boolean
    ): Boolean {
        val vppId =
            if (vppId) vppIdRepository.getVppIds().first().firstOrNull { it.isActive() }
            else null
        return false
    }
}