package es.jvbabi.vplanplus.domain.usecase.sync

data class SyncUseCases(
    val triggerSyncUseCase: TriggerSyncUseCase,
    val isSyncRunningUseCase: IsSyncRunningUseCase,
    val doWorkUseCase: DoSyncUseCase
)