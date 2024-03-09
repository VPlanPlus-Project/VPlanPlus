package es.jvbabi.vplanplus.feature.grades.domain.usecase

import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.BiometricStatus

class IsBiometricSetUpUseCase(
    private val biometricRepository: BiometricRepository
) {
    operator fun invoke() = biometricRepository.canAuthenticate() != BiometricStatus.NOT_SET_UP
}