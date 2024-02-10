package es.jvbabi.vplanplus

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.hilt.android.HiltAndroidApp
import es.jvbabi.vplanplus.domain.usecase.sync.SyncUseCases
import es.jvbabi.vplanplus.worker.SyncWorker
import javax.inject.Inject

@HiltAndroidApp
class VppApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var syncUseCases: SyncUseCases

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(SyncWorkerFactory(syncUseCases = syncUseCases))
            .build()
}

class SyncWorkerFactory @Inject constructor(
    private val syncUseCases: SyncUseCases
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): SyncWorker {
        return SyncWorker(
            context = appContext,
            params = workerParameters,
            syncUseCases = syncUseCases
        )
    }
}