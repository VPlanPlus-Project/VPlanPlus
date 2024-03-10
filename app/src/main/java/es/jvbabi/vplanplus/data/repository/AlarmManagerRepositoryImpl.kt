package es.jvbabi.vplanplus.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import es.jvbabi.vplanplus.android.receiver.AlarmReceiver
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository

class AlarmManagerRepositoryImpl(
    private val context: Context
) : AlarmManagerRepository {

    private val service = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun setAlarm(epochSecond: Long, tag: String, data: String) {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("tag", tag)
            putExtra("data", data)
        }

        val canSendAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) service.canScheduleExactAlarms() else true
        if (canSendAlarm) service.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            epochSecond * 1000,
            PendingIntent.getBroadcast(context, data.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        ) else {
            Log.e("AlarmManagerRepositoryImpl", "Can't send alarm")
        }
    }

    override fun cancelAlarm(data: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, data.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { service.cancel(it) }
    }

    override fun canRequestAlarm(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) service.canScheduleExactAlarms() else true
    }
}