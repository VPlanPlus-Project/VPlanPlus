package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Alarm
import java.time.ZonedDateTime

interface AlarmManagerRepository {
    fun canRequestAlarm(): Boolean

    suspend fun addAlarm(time: ZonedDateTime, tags: List<String>, data: String): Alarm
    suspend fun deleteAlarmById(id: Int)
    suspend fun deleteAlarmsByTag(tag: String)
    suspend fun deleteIf(predicate: (Alarm) -> Boolean)

    suspend fun getAlarmById(id: Int): Alarm?

    companion object {
        const val TAG_HOMEWORK_NOTIFICATION = "homework_notification"
    }
}