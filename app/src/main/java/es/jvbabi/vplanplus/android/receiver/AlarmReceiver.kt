package es.jvbabi.vplanplus.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository.Companion.TAG_DAILY_REMINDER
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.daily.DailyReminderNotificationData
import es.jvbabi.vplanplus.domain.usecase.daily.SendNotificationUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var sendDailyNotificationUseCase: SendNotificationUseCase
    @Inject lateinit var alarmManagerRepository: AlarmManagerRepository
    @Inject lateinit var profileRepository: ProfileRepository

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("AlarmReceiver", "onReceive")
        if (context == null || intent == null) run {
            Log.e("AlarmReceiver", "Context or intent is null")
            return
        }

        val alarmId = intent.getIntExtra("id", -1).let { if (it == -1) null else it } ?: run {
            Log.e("AlarmReceiver", "Intent does not contain id")
            return
        }
        Log.i("AlarmReceiver", "Alarm id: $alarmId")

        GlobalScope.launch {
            val alarm = alarmManagerRepository.getAlarmById(alarmId) ?: run {
                Log.e("AlarmReceiver", "Alarm with id $alarmId not found")
                return@launch
            }
            Log.i("AlarmReceiver", "Alarm found: $alarm")
            Log.i("AlarmReceiver", "Alarm tags: ${alarm.tags}")
            if (alarm.tags.contains(TAG_DAILY_REMINDER)) {
                Log.i("AlarmReceiver", "Alarm with id $alarmId is a daily reminder")
                val payload = Json.decodeFromString<DailyReminderNotificationData>(alarm.data)
                val profile = profileRepository.getProfileById(UUID.fromString(payload.profileId)).first() as? ClassProfile ?: return@launch
                sendDailyNotificationUseCase(profile, payload.dismissCounter)
            }
            alarmManagerRepository.deleteAlarmById(alarmId)
        }
    }
}