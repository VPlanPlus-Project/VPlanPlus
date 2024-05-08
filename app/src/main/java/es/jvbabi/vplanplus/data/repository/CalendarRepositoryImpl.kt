package es.jvbabi.vplanplus.data.repository

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import androidx.core.database.getStringOrNull
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.CalendarEvent
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.util.TimeZone

class CalendarRepositoryImpl(
    val context: Context
) : CalendarRepository {

    @SuppressLint("Range")
    override fun getCalendars(): Flow<List<Calendar>> = flow {
        while (true) {
            val calendars = mutableListOf<Calendar>()
            val contentResolver = context.contentResolver
            val calendarsUri = CalendarContract.Calendars.CONTENT_URI

            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT
            )
            try {
                val cursor = contentResolver.query(calendarsUri, projection, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val calendarId =
                            cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
                        val calendarName =
                            cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                        val owner =
                            cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT))
                        calendars.add(Calendar(calendarId, calendarName, owner))
                    } while (cursor.moveToNext())
                    cursor.close()
                }
            } catch (_: SecurityException) {
            }
            emit(calendars)
        }
    }

    override suspend fun getCalendarById(id: Long): Calendar? {
        return getCalendars().firstOrNull()?.firstOrNull { it.id == id }
    }

    override suspend fun insertEvent(event: CalendarEvent): Long? {
        val info = (event.info ?: "Keine Informationen - No Information") + "\n\n Bearbeite dieses Event NICHT. Andernfalls findet VPlanPlus dieses Ereignis nicht mehr.\nDO NOT modify this event. Otherwise, VPlanPlus will fail to find this event. | VPlanPlus"
        val contentResolver = context.contentResolver
        val values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, event.startTimeStamp * 1000)
        values.put(CalendarContract.Events.DTEND, event.endTimeStamp * 1000)
        values.put(CalendarContract.Events.TITLE, event.title)
        values.put(CalendarContract.Events.EVENT_LOCATION, event.location)
        values.put(CalendarContract.Events.CALENDAR_ID, event.calendarId)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, event.timeZone.id)
        values.put(CalendarContract.Events.DESCRIPTION, info)

        return contentResolver.insert(
            CalendarContract.Events.CONTENT_URI,
            values
        )?.lastPathSegment?.toLong() ?: return null
    }

    @SuppressLint("Range")
    override suspend fun getAppEvents(calendar: Calendar): List<CalendarEvent> {
        val contentResolver = context.contentResolver
        val eventsUri = CalendarContract.Events.CONTENT_URI
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.EVENT_LOCATION
        )

        val cursor = contentResolver.query(
            eventsUri,
            projection,
            "${CalendarContract.Events.CALENDAR_ID} = ?",
            arrayOf(calendar.id.toString()),
            null
        ) ?: return emptyList()
        cursor.moveToFirst()
        val events = mutableListOf<CalendarEvent>()
        if (cursor.count > 0) do {
            if (cursor.columnCount == 0) continue
            val id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID))
            val title = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE))
            val description = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION))
            val start = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART))
            val end = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND))
            val timeZone = cursor.getStringOrNull(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE))
            val location = cursor.getStringOrNull(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION))
            events.add(
                CalendarEvent(
                    eventId = id,
                    title = title,
                    location = location,
                    startTimeStamp = start / 1000,
                    endTimeStamp = end / 1000,
                    calendarId = calendar.id,
                    timeZone = TimeZone.getTimeZone(timeZone ?: "UTC"),
                    info = description,
                )
            )
        } while (cursor.moveToNext())
        cursor.close()
        return events.filter { it.info?.endsWith("| VPlanPlus") == true }
    }

    override suspend fun deleteEvent(id: Long) {
        val contentResolver = context.contentResolver
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id)
        contentResolver.delete(deleteUri, null, null)
    }

    override suspend fun deleteAppEvents(calendar: Calendar?) {
        Log.d("CalendarRepositoryImpl", "deleteAppEvents: ${calendar?.displayName}")
        val calendars = if (calendar == null) getCalendars().first() else listOfNotNull(calendar)
        Log.d("CalendarRepositoryImpl", "deleteAppEvents: ${calendars.joinToString(", ") { it.displayName }}")
        val eventIds = calendars.flatMap { getAppEvents(it) }.mapNotNull { it.eventId }
        Log.d("CalendarRepositoryImpl", "deleteAppEvents: ${eventIds.joinToString(", ")}")
        eventIds.forEach { deleteEvent(it) }
    }
}