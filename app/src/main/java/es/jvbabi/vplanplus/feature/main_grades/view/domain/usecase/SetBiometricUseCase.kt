package es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class SetBiometricUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(to: Boolean) {
        keyValueRepository.set(Keys.GRADES_BIOMETRIC_ENABLED, value = to.toString())
    }
}