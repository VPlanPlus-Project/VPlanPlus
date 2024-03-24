package es.jvbabi.vplanplus.feature.main_grades.domain.usecase

import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.BiometricStatus
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class CanShowEnableBiometricBannerUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val biometricRepository: BiometricRepository
) {
     operator fun invoke() = flow {
         combine(
             keyValueRepository.getFlowOrDefault(Keys.SHOW_ENABLE_BIOMETRIC_BANNER, "true"),
             keyValueRepository.getFlowOrDefault(Keys.GRADES_BIOMETRIC_ENABLED, "false")
         ) { showBanner, biometricEnabled ->
             showBanner.toBoolean() && !biometricEnabled.toBoolean() && biometricRepository.canAuthenticate() != BiometricStatus.NOT_SUPPORTED
         }.collect {
             emit(it)
         }
     }
}