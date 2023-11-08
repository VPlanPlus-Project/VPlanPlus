package es.jvbabi.vplanplus.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import java.time.LocalDate

class SyncWorker @AssistedInject constructor(
    private val context: Context,
    private val params: WorkerParameters,
    @Assisted private val profileUseCases: ProfileUseCases,
    @Assisted private val vPlanUseCases: VPlanUseCases
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("SyncWorker", "SYNCING")
        profileUseCases.getProfiles().forEach { profile ->
            val school = profileUseCases.getSchoolFromProfileId(profile.id!!)
            Log.d("SyncWorker", "Syncing ${profile.name}")
            val data = vPlanUseCases.getVPlanData(school, LocalDate.now())

            val hashBefore = profileUseCases.getPlanSum(profile, LocalDate.now()) // TODO do for entire week
            if (data.response != Response.SUCCESS) return Result.retry()
            vPlanUseCases.processVplanData(data.data!!)
            val hashAfter = profileUseCases.getPlanSum(profile, LocalDate.now())

            Log.d("SyncWorker", "Hash before: $hashBefore, hash after: $hashAfter")

            if (hashBefore != hashAfter) {
                Log.d("SyncWorker", "Hashes are different, updating profile")
            }
        }
        return Result.success()
    }


}