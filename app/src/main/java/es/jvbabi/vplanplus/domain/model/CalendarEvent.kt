package es.jvbabi.vplanplus.domain.model

import java.util.TimeZone

/**
 * Represents a calendar event. This class can be used to create or retrieve events from a calendar.
 * @param eventId The id of the event. If the event is not created yet, this value should be null.
 * @param title The title of the event.
 * @param startTimeStamp The start time of the event in seconds (UTC)
 * @param endTimeStamp The end time of the event in seconds (UTC)
 * @param timeZone The time zone of the event. (Preferably UTC)
 * @param calendarId The id of the calendar where the event is stored.
 * @param location The location of the event.
 * @param info Additional information about the event. A suffix will be added to identify events created by the app.
 */
data class CalendarEvent(
    val eventId: Long? = null,
    val title: String,
    val startTimeStamp: Long,
    val endTimeStamp: Long,
    val timeZone: TimeZone = TimeZone.getTimeZone("UTC"),
    val calendarId: Long,
    val location: String,
    val info: String? = null,
)