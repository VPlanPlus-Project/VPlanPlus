package es.jvbabi.vplanplus.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object DateUtils {

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

    @SuppressLint("SimpleDateFormat")
    fun calculateProgress(start: String, current: String, end: String): Double? {
        return try {
            val dateFormat = SimpleDateFormat("HH:mm")
            val startTime = dateFormat.parse(start)!!
            val currentTime = dateFormat.parse(current)!!
            val endTime = dateFormat.parse(end)!!

            val totalTime = (endTime.time - startTime.time).toDouble()
            val elapsedTime = (currentTime.time - startTime.time).toDouble()

            (elapsedTime / totalTime)
        } catch (e: ParseException) {
            null
        }
    }

    fun getTimestampFromTimeString(timeString: String, localDate: LocalDate): Long {
        val time = timeString.split(":")
        val hour = time[0].toInt()
        val minute = time[1].toInt()
        val localDateTime = LocalDateTime.of(localDate.year, localDate.month, localDate.dayOfMonth, hour, minute)
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()/1000
    }
}