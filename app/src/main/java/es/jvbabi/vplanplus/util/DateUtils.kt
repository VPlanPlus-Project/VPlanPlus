package es.jvbabi.vplanplus.util

import android.content.Context
import es.jvbabi.vplanplus.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle


object DateUtils {

    fun getDateFromTimestamp(timestamp: Long): LocalDate {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun getDateTimeFromTimestamp(timestamp: Long): LocalDateTime {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun ZonedDateTime.progress(start: ZonedDateTime, end: ZonedDateTime): Float {
        val from = start.toInstant().epochSecond
        val now = this.toInstant().epochSecond
        val to = end.toInstant().epochSecond
        return (now - from) / (to - from).toFloat()
    }

    fun ZonedDateTime.atBeginningOfTheWorld(): ZonedDateTime {
        return ZonedDateTime.of(1970, 1, 1, this.hour, this.minute, this.second, this.nano, this.zone)
    }

    fun localizedRelativeDate(context: Context, localDate: LocalDate, fallbackToDefaultFormatting: Boolean = true): String? {
        val result = when (localDate) {
            LocalDate.now() -> {
                context.getString(R.string.today)
            }
            LocalDate.now().plusDays(1) -> {
                context.getString(R.string.tomorrow)
            }
            LocalDate.now().plusDays(2) -> {
                context.getString(R.string.day_after_tomorrow)
            }
            LocalDate.now().minusDays(1) -> {
                context.getString(R.string.yesterday)
            }
            LocalDate.now().minusDays(2) -> {
                context.getString(R.string.day_before_yesterday)
            }
            else -> {
                if (fallbackToDefaultFormatting) localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                else null
            }
        }
        return if ((result == null || result == ";DATE") && fallbackToDefaultFormatting) {
            localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        } else {
            result
        }
    }

    fun String.toZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
        val localDateTime = try {
            LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        } catch (e: DateTimeParseException) {
            LocalDateTime.parse("1970-01-01 $this", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }
        return localDateTime.atZone(zoneId)
    }


    fun LocalDate.getRelativeStringResource(
        contextDate: LocalDate = LocalDate.now()
    ): Int? {
        return when (this) {
            contextDate -> {
                R.string.today
            }
            contextDate.plusDays(1) -> {
                R.string.tomorrow
            }
            contextDate.plusDays(2) -> {
                R.string.day_after_tomorrow
            }
            contextDate.minusDays(1) -> {
                R.string.yesterday
            }
            contextDate.minusDays(2) -> {
                R.string.day_before_yesterday
            }
            else -> {
                null
            }
        }
    }

    fun ZonedDateTime.between(start: ZonedDateTime, end: ZonedDateTime): Boolean {
        return (this.isAfter(start) || this.isEqual(start)) && this.isBefore(end)
    }

    fun zonedDateFromTimeStringAndDate(timeString: String, date: LocalDate): ZonedDateTime {
        val time = timeString.split(":")
        val hour = time[0].toInt()
        val minute = time[1].toInt()
        return ZonedDateTime.of(date.year, date.monthValue, date.dayOfMonth, hour, minute, 0, 0, ZoneId.systemDefault())
    }

    fun ZonedDateTime.toZonedLocalDateTime(): LocalDateTime {
        val zoned = this.withZoneSameInstant(ZoneId.systemDefault())
        return zoned.toLocalDateTime()
    }
}