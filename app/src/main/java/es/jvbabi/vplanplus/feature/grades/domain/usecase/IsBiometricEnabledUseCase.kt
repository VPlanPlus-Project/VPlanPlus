package es.jvbabi.vplanplus.feature.grades.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow

class IsBiometricEnabledUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = flow {
        keyValueRepository.getFlowOrDefault(Keys.GRADES_BIOMETRIC_ENABLED, "true").collect {
            emit(it.toBoolean())
        }
    }
}