package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.usecase.sync.UpdateFirebaseTokenUseCase

class UpdateFcmTokenUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase
) {
    suspend operator fun invoke(): Boolean {
        val token = keyValueRepository.get(Keys.FCM_TOKEN) ?: return false
        return updateFirebaseTokenUseCase(token)
    }
}