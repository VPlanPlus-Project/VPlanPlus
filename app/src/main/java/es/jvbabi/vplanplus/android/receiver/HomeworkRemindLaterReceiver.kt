package es.jvbabi.vplanplus.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import javax.inject.Inject

@AndroidEntryPoint
class HomeworkRemindLaterReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmManagerRepository: AlarmManagerRepository
    @Inject lateinit var notificationRepository: NotificationRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            Log.i("HomeworkRemindLaterReceiver", "Intent or context is null")
            return
        }

        val tag = intent.getStringExtra("tag") ?: ""
        if (tag != TAG) return
        Log.i("HomeworkRemindLaterReceiver", "Homework remind later")

        alarmManagerRepository.setAlarm(
            now() + halfHour,
            AlarmManagerRepository.TAG_HOMEWORK_NOTIFICATION,
            "HOMEWORK_REMINDER_LATER"
        )
        notificationRepository.dismissNotification(NotificationRepository.CHANNEL_HOMEWORK_REMINDER_NOTIFICATION_ID)
    }

    private fun now() = System.currentTimeMillis() / 1000
    private val halfHour = 60 * 30

    companion object {
        const val TAG = "HomeworkRemindLaterReceiver"
    }
}