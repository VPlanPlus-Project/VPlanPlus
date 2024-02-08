package es.jvbabi.vplanplus.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.sync.SyncUseCases

class SyncWorker @AssistedInject constructor(
    private val context: Context,
    params: WorkerParameters,
    @Assisted private val syncUseCases: SyncUseCases
) : CoroutineWorker(context, params) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            1,
            NotificationCompat.Builder(context, "SYNC")
                .setContentTitle("VPlanPlus")
                .setContentText("Synchronisiere...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        )
    }

    override suspend fun doWork(): Result {
        if (syncUseCases.doWorkUseCase()) return Result.success()
        return Result.failure()
    }
}
