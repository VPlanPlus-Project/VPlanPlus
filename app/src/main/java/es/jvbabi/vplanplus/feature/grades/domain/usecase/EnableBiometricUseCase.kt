package es.jvbabi.vplanplus.feature.grades.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class EnableBiometricUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.GRADES_BIOMETRIC_ENABLED, value = "true")
    }
}