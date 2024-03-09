package es.jvbabi.vplanplus.domain.usecase.settings.advanced

import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase

data class AdvancedSettingsUseCases(
    val deleteCacheUseCase: DeleteCacheUseCase,
    val getVppIdServerUseCase: GetVppIdServerUseCase,
    val setVppIdServerUseCase: SetVppIdServerUseCase
)