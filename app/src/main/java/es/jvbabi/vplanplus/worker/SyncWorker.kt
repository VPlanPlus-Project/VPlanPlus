package es.jvbabi.vplanplus.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class SyncWorker @AssistedInject constructor(
    private val context: Context,
    private val params: WorkerParameters,
    @Assisted private val profileRepository: ProfileRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("SyncWorker", "SYNCING")
        Log.d("SyncWorker", "Profiles: ${profileRepository.getProfiles()}")
        return Result.success()
    }


}