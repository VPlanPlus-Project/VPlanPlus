package es.jvbabi.vplanplus.util

import java.time.LocalDate
import java.time.ZoneId

object DateUtils {
    fun getCurrentDayTimestamp(): Long {
        val currentLocalDate = LocalDate.now()
        val startOfDay = currentLocalDate.atStartOfDay(ZoneId.systemDefault())
        return startOfDay.toInstant().toEpochMilli()/1000
    }

    fun getDayTimestamp(year: Int, month: Int, day: Int): Long {
        val localDate = LocalDate.of(year, month, day)
        val startOfDay = localDate.atStartOfDay(ZoneId.systemDefault())
        return startOfDay.toInstant().toEpochMilli()/1000
    }
}