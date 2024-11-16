package es.jvbabi.vplanplus.domain.usecase.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.flow.firstOrNull

class TriggerSyncUseCase(
    private val context: Context,
    private val isSyncRunningUseCase: IsSyncRunningUseCase
) {
    suspend operator fun invoke(force: Boolean = false) {
        if (isSyncRunningUseCase().firstOrNull() == true && !force) return

        if (force) WorkManager.getInstance(context).cancelAllWorkByTag("SyncWork")

        val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag("SyncWork")
            .addTag("ManualSyncWork")
            .build()
        WorkManager.getInstance(context).enqueue(syncWork)
    }
}