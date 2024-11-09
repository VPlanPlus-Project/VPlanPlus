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
import es.jvbabi.vplanplus.domain.usecase.daily.DailyReminderNotificationData
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class DailyRemindLaterReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmManagerRepository: AlarmManagerRepository
    @Inject
    lateinit var notificationRepository: NotificationRepository
    @Inject
    lateinit var keyValueRepository: KeyValueRepository

    companion object {
        const val TAG = "DailyRemindLaterReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            Log.i(this::class.java.simpleName, "Intent or context is null")
            return
        }

        val tag = intent.getStringExtra("tag") ?: ""
        if (tag != TAG) return

        val payload = (intent.getStringExtra("payload") ?: return).let {
            Log.i(this::class.java.simpleName, "Payload: $it")
            Json.decodeFromString<DailyReminderNotificationData>(it)
        }
        Log.i(this::class.java.simpleName, "Daily remind later clicked for payload $payload")

        runBlocking {
            val time = ZonedDateTime.now().plusSeconds(Keys.SETTINGS_DAILY_LATER_DEFAULT_SECONDS.toLong())
            alarmManagerRepository.addAlarm(
                time = time,
                tags = listOf(AlarmManagerRepository.TAG_DAILY_REMINDER, AlarmManagerRepository.TAG_DAILY_REMINDER_DELAYED),
                data = Json.encodeToString(DailyReminderNotificationData(payload.profileId, payload.dismissCounter + 1)),
            )
            Log.i("DailyRemindLaterReceiver", "Added alarm for $time, currentDismissCounter = ${payload.dismissCounter+1}")
        }

        intent.getIntExtra("notificationId", -1).let { notificationId ->
            if (notificationId == -1) return@let
            notificationRepository.dismissNotification(notificationId)
            Log.i("DailyRemindLaterReceiver", "Dismissed notification $notificationId")
        }
    }
}