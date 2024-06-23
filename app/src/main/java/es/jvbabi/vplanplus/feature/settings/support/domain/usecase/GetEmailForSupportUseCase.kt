package es.jvbabi.vplanplus.feature.settings.support.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.flow

class GetEmailForSupportUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
) {
    operator fun invoke() = flow {
        getCurrentProfileUseCase().collect {
            emit((it as? ClassProfile)?.vppId?.email)
        }
    }
}