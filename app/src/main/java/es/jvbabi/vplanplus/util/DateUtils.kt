package es.jvbabi.vplanplus.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
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

    fun getDayTimestamp(localDate: LocalDate): Long {
        return getDayTimestamp(localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    fun getDateFromTimestamp(timestamp: Long): LocalDate {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun LocalDate.atStartOfWeek(): LocalDate {
        return this.minusDays(this.dayOfWeek.value.toLong() - 1)
    }

    fun getDateTimeFromTimestamp(timestamp: Long): LocalDateTime {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}