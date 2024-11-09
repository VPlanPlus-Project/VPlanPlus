package es.jvbabi.vplanplus.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import es.jvbabi.vplanplus.android.receiver.AlarmReceiver
import es.jvbabi.vplanplus.data.source.database.dao.AlarmDao
import es.jvbabi.vplanplus.domain.model.Alarm
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import java.time.ZonedDateTime

class AlarmManagerRepositoryImpl(
    private val context: Context,
    private val alarmDao: AlarmDao
) : AlarmManagerRepository {

    private val service = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun canRequestAlarm(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) service.canScheduleExactAlarms() else true
    }

    override suspend fun addAlarm(time: ZonedDateTime, tags: List<String>, data: String): Alarm {
        val id = alarmDao.insert(
            time = time,
            tags = tags.joinToString(";"),
            data = data
        ).toInt()
        val alarm = alarmDao.getAlarmById(id)!!.toModel()
        createSystemAlarm(context, alarm)
        return alarm
    }

    override suspend fun deleteAlarmById(id: Int) {
        deleteAlarmById(id, true)
    }

    override suspend fun deleteAlarmsByTag(tag: String) {
        alarmDao
            .getAlarms()
            .filter { it.tags.contains(tag) }
            .forEach { deleteAlarmById(it.id, false) }
        rebuild()
    }

    override suspend fun deleteIf(predicate: (Alarm) -> Boolean) {
        alarmDao.getAlarms().map { it.toModel() }.filter(predicate).forEach { deleteAlarmById(it.id, false) }
    }

    private suspend fun deleteAlarmById(id: Int, autoRebuild: Boolean = true) {
        alarmDao.delete(id)
        if (autoRebuild) rebuild()
    }

    private suspend fun rebuild() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) service.cancelAll()
        alarmDao.deleteOld(ZonedDateTime.now())
        alarmDao
            .getAlarms()
            .map { it.toModel() }
            .forEach { alarm ->
                createSystemAlarm(context, alarm)
            }
    }

    private fun createSystemAlarm(context: Context, alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", alarm.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                service.canScheduleExactAlarms()
            } else {
                false
            }
        ) service.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            alarm.time.toEpochSecond() * 1000,
            pendingIntent
        ) else service.setAndAllowWhileIdle(
            AlarmManager.RTC,
            alarm.time.toEpochSecond() * 1000,
            pendingIntent
        )
    }

    override suspend fun getAlarmById(id: Int): Alarm? {
        return alarmDao.getAlarmById(id)?.toModel()
    }
}