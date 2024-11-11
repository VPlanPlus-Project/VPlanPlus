package es.jvbabi.vplanplus.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class HomeworkRemindLaterReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmManagerRepository: AlarmManagerRepository
    @Inject lateinit var notificationRepository: NotificationRepository
    @Inject lateinit var keyValueRepository: KeyValueRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            Log.i("HomeworkRemindLaterReceiver", "Intent or context is null")
            return
        }

        val tag = intent.getStringExtra("tag") ?: ""
        if (tag != TAG) return
        Log.i("HomeworkRemindLaterReceiver", "Homework remind later")


        runBlocking {
            alarmManagerRepository.addAlarm(
                time = ZonedDateTime.now().plusSeconds(keyValueRepository.getOrDefault(
                    Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_LATER_SECONDS,
                    Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_LATER_SECONDS_DEFAULT
                ).toLong()),
                tags = listOf(AlarmManagerRepository.TAG_HOMEWORK_NOTIFICATION),
                data = "HOMEWORK_REMINDER_LATER",
            )
        }
        notificationRepository.dismissNotification(NotificationRepository.CHANNEL_HOMEWORK_REMINDER_NOTIFICATION_ID)
    }

    companion object {
        const val TAG = "HomeworkRemindLaterReceiver"
    }
}