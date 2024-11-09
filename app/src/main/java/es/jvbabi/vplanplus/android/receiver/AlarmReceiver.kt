package es.jvbabi.vplanplus.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository.Companion.TAG_DAILY_REMINDER
import es.jvbabi.vplanplus.domain.usecase.daily.SendNotificationUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var sendDailyNotificationUseCase: SendNotificationUseCase
    @Inject lateinit var alarmManagerRepository: AlarmManagerRepository

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) run {
            Log.e("AlarmReceiver", "Context or intent is null")
            return
        }

        val alarmId = intent.getStringExtra("id")?.toInt() ?: run {
            Log.e("AlarmReceiver", "Intent does not contain id")
            return
        }

        GlobalScope.launch {
            val alarm = alarmManagerRepository.getAlarmById(alarmId) ?: run {
                Log.e("AlarmReceiver", "Alarm with id $alarmId not found")
                return@launch
            }
            if (alarm.tags.contains(TAG_DAILY_REMINDER)) {
                sendDailyNotificationUseCase()
            }
        }
    }
}