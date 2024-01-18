package es.jvbabi.vplanplus.domain.usecase.general.data

data class SyncUseCases(
    val runSyncUseCase: RunSyncUseCase,
    val isSyncRunningUseCase: IsSyncRunningUseCase
)