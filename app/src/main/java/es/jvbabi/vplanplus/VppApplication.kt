package es.jvbabi.vplanplus

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.hilt.android.HiltAndroidApp
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.worker.SyncWorker
import javax.inject.Inject

@HiltAndroidApp
class VppApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var profileRepository: ProfileRepository

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(SyncWorkerFactory(profileRepository))
        .build()
}

class SyncWorkerFactory @Inject constructor(
    private val profileRepository: ProfileRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): SyncWorker {
        return SyncWorker(appContext, workerParameters, profileRepository)
    }
}