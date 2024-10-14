package es.jvbabi.vplanplus.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository.Companion.TAG_HOMEWORK_NOTIFICATION
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository.Companion.TAG_NDP_REMINDER
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.HomeworkReminderUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.TriggerNdpReminderNotificationUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var homeworkReminderUseCase: HomeworkReminderUseCase
    @Inject lateinit var triggerNdpReminderNotificationUseCase: TriggerNdpReminderNotificationUseCase

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) run {
            Log.e("AlarmReceiver", "Context or intent is null")
            return
        }

        val tag = intent.getStringExtra("tag") ?: run {
            Log.e("AlarmReceiver", "Tag is null")
            return
        }

        Log.i("AlarmReceiver", "Alarm triggered with tag $tag")
        GlobalScope.launch {
            when (tag) {
                TAG_HOMEWORK_NOTIFICATION -> {
                    homeworkReminderUseCase()
                    Log.i("AlarmReceiver", "Homework alarm triggered")
                }
                TAG_NDP_REMINDER -> {
                    triggerNdpReminderNotificationUseCase()
                }
            }
        }
    }
}