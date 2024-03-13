package es.jvbabi.vplanplus.feature.settings.support.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import kotlinx.coroutines.flow.flow

class GetEmailForSupportUseCase(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
) {
    operator fun invoke() = flow {
        getCurrentIdentityUseCase().collect {
            emit(it?.vppId?.email)
        }
    }
}