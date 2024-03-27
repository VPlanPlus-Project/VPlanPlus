package es.jvbabi.vplanplus.feature.main_home.feature_search.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase

data class SearchUseCases(
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val isSyncRunningUseCase: IsSyncRunningUseCase,
    val searchUseCase: SearchUseCase,
    val getCurrentTimeUseCase: GetCurrentTimeUseCase
)
