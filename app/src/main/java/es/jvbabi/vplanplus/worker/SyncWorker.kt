package es.jvbabi.vplanplus.worker

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import java.time.LocalDate


class SyncWorker @AssistedInject constructor(
    private val context: Context,
    params: WorkerParameters,
    @Assisted private val profileUseCases: ProfileUseCases,
    @Assisted private val schoolUseCases: SchoolUseCases,
    @Assisted private val vPlanUseCases: VPlanUseCases,
    @Assisted private val keyValueUseCases: KeyValueUseCases,
    @Assisted private val logRecordRepository: LogRecordRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        Log.d("SyncWorker", "SYNCING")
        logRecordRepository.log("SyncWorker", "Syncing")
        val planIsChanged = hashMapOf<Profile, Boolean>()
        val syncDays = (keyValueUseCases.get(Keys.SETTINGS_SYNC_DAY_DIFFERENCE) ?: "3").toInt()
        schoolUseCases.getSchools().forEach { school ->
            repeat(syncDays + 2) { i ->
                val profiles = profileUseCases.getProfilesBySchoolId(school.id!!)
                val date = LocalDate.now().plusDays(i - 2L)
                val hashesBefore = hashMapOf<Profile, String>()
                val hashesAfter = hashMapOf<Profile, String>()
                profiles.forEach { profile ->
                    hashesBefore[profile] = profileUseCases.getPlanSum(profile, date)
                }
                val data = vPlanUseCases.getVPlanData(school, date)
                Log.d(
                    "SyncWorker",
                    "Syncing ${school.id} (${school.name}) at $date: ${data.response}"
                )
                if (!listOf(Response.SUCCESS, Response.NO_DATA_AVAILABLE).contains(data.response)) {
                    logRecordRepository.log(
                        "SyncWorker",
                        "Error while syncing ${school.id} (${school.name}): ${data.response}"
                    )
                    return Result.failure()
                }
                if (data.response == Response.NO_DATA_AVAILABLE) {
                    logRecordRepository.log(
                        "SyncWorker",
                        "No data available for ${school.id} (${school.name} at $date)"
                    )
                    return@repeat
                }
                vPlanUseCases.processVplanData(data.data!!)
                profiles.forEach { profile ->
                    hashesAfter[profile] = profileUseCases.getPlanSum(profile, date)
                }
                profiles.forEach { profile ->
                    if (hashesBefore[profile] != hashesAfter[profile] && (!isAppInForeground() || keyValueUseCases.get(
                            Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE
                        ) == "true")
                    ) {
                        planIsChanged[profile] = true
                    }
                }
            }
        }
        Log.d("SyncWorker", "Changed profiles: ${planIsChanged.keys.map { it.name }}")
        planIsChanged.keys.forEach { profile ->
            if (planIsChanged[profile] == true) sendNewPlanNotification(profile)
        }

        Log.d("SyncWorker", "SYNCED")
        logRecordRepository.log("SyncWorker", "Synced sucessfully")
        return Result.success()
    }

    private suspend fun sendNewPlanNotification(profile: Profile) {
        logRecordRepository.log("SyncWorker", "Sending notification for profile ${profile.name}")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.putExtra("profileId", profile.id)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val school = profileUseCases.getSchoolFromProfileId(profile.id!!)
        val builder = NotificationCompat.Builder(context, "PROFILE_${profile.name}")
            .setContentTitle(context.getString(R.string.notification_newPlanTitle))
            .setContentText(
                context.getString(
                    R.string.notification_newPlanText,
                    profile.name,
                    school.name
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(profile.id.toInt(), builder.build())
    }

    private fun isAppInForeground(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE)
    }
}