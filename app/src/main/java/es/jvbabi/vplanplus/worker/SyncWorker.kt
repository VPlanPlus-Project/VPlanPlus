package es.jvbabi.vplanplus.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import es.jvbabi.vplanplus.domain.model.Profile
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
        sendNewPlanNotification(profileUseCases.getActiveProfile()!!)
        profileUseCases.getProfiles().forEach { profile ->
            repeat(2) { i ->
                val school = profileUseCases.getSchoolFromProfileId(profile.id!!)
                Log.d("SyncWorker", "Syncing ${profile.name}@${school.name} for ${LocalDate.now().plusDays(i.toLong())}")
                val data = vPlanUseCases.getVPlanData(school, LocalDate.now().plusDays(i.toLong()))

                val hashBefore = profileUseCases.getPlanSum(profile, LocalDate.now().plusDays(i.toLong())) // TODO do for entire week
                if (data.response != Response.SUCCESS) return Result.retry()
                vPlanUseCases.processVplanData(data.data!!)
                val hashAfter = profileUseCases.getPlanSum(profile, LocalDate.now().plusDays(i.toLong()))

                Log.d("SyncWorker", "Hash before: ${hashBefore.substring(0..10)}..., hash after: ${hashAfter.substring(0..10)}...")

                if (hashBefore != hashAfter) {
                    Log.d("SyncWorker", "Hashes are different, updating profile")
                }
            }
        }
        Log.d("SyncWorker", "SYNCED")
        return Result.success()
    }

    private fun sendNewPlanNotification(profile: Profile) {
    }
}