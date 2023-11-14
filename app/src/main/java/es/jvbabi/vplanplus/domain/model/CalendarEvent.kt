package es.jvbabi.vplanplus.domain.model

import java.time.LocalDate
import java.util.TimeZone

data class CalendarEvent(
    val title: String,
    val startTimeStamp: Long,
    val endTimeStamp: Long,
    val timeZone: TimeZone = TimeZone.getDefault(),
    val calendarId: Long,
    val location: String,
    val date: LocalDate
)