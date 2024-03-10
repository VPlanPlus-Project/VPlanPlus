package es.jvbabi.vplanplus.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository.Companion.TAG_HOMEWORK_NOTIFICATION

class AlarmReceiver : BroadcastReceiver() {
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
        when (tag) {
            TAG_HOMEWORK_NOTIFICATION -> {
                Log.i("AlarmReceiver", "Homework alarm triggered")
            }
        }
    }
}