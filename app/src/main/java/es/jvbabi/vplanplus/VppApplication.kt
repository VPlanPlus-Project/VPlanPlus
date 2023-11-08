package es.jvbabi.vplanplus

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.hilt.android.HiltAndroidApp
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import es.jvbabi.vplanplus.worker.SyncWorker
import javax.inject.Inject

@HiltAndroidApp
class VppApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var profileUseCases: ProfileUseCases

    @Inject
    lateinit var vPlanUseCases: VPlanUseCases

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(SyncWorkerFactory(profileUseCases, vPlanUseCases))
        .build()
}

class SyncWorkerFactory @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val vPlanUseCases: VPlanUseCases
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): SyncWorker {
        return SyncWorker(appContext, workerParameters, profileUseCases, vPlanUseCases)
    }
}