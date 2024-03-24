package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class ChangeProfileUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(profileId: String) {
        keyValueRepository.set(Keys.ACTIVE_PROFILE, profileId)
    }
}