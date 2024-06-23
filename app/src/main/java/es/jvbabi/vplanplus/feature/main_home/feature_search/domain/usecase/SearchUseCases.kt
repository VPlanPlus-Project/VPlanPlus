package es.jvbabi.vplanplus.feature.main_home.feature_search.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase

data class SearchUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val isSyncRunningUseCase: IsSyncRunningUseCase,
    val searchUseCase: SearchUseCase,
    val getCurrentTimeUseCase: GetCurrentTimeUseCase
)
