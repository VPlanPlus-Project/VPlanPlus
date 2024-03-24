package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository

class UpdateFcmTokenUseCase(
    private val firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
) {
    suspend operator fun invoke() = firebaseCloudMessagingManagerRepository.updateToken(null)
}