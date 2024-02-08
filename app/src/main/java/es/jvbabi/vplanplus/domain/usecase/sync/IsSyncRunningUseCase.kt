package es.jvbabi.vplanplus.domain.usecase.sync

import android.content.Context
import es.jvbabi.vplanplus.util.Worker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class IsSyncRunningUseCase(
    private val context: Context,
) {
    operator fun invoke(): Flow<Boolean> {
        return Worker.isWorkerRunningFlow("SyncWork", context).distinctUntilChanged()
    }
}