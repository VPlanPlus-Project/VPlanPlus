package es.jvbabi.vplanplus.feature.home.domain.usecase

import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class UpdateLastVersionHintsVersionUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.LAST_VERSION_HINTS_VERSION, BuildConfig.VERSION_CODE.toString())
    }
}