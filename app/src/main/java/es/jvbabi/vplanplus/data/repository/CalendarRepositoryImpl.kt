package es.jvbabi.vplanplus.data.repository

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import es.jvbabi.vplanplus.data.source.database.dao.CalendarEventDao
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.CalendarEvent
import es.jvbabi.vplanplus.domain.model.DbCalendarEvent
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class CalendarRepositoryImpl(
    val context: Context,
    val calendarEventDao: CalendarEventDao
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
            emit(calendars)
        }
    }

    override suspend fun getCalendarById(id: Long): Calendar? {
        return getCalendars().firstOrNull()?.firstOrNull { it.id == id }
    }

    override suspend fun insertEvent(event: CalendarEvent, school: School): Long? {
        val contentResolver = context.contentResolver
        val values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, event.startTimeStamp * 1000)
        values.put(CalendarContract.Events.DTEND, event.endTimeStamp * 1000)
        values.put(CalendarContract.Events.TITLE, event.title)
        values.put(CalendarContract.Events.DESCRIPTION, event.location)
        values.put(CalendarContract.Events.CALENDAR_ID, event.calendarId)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, event.timeZone.id)

        val id = contentResolver.insert(
            CalendarContract.Events.CONTENT_URI,
            values
        )?.lastPathSegment?.toLong() ?: return null
        calendarEventDao.insertCalendarEvent(
            DbCalendarEvent(
                date = event.date,
                schoolCalendarEventRefId = school.schoolId,
                calendarId = id
            )
        )
        return id
    }

    override suspend fun deleteCalendarEvents(school: School, date: LocalDate) {
        val calendarEvents = calendarEventDao.getCalendarEvents(date = date, schoolId = school.schoolId)
        val contentResolver = context.contentResolver
        calendarEvents.forEach {
            val deleteUri =
                ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, it.calendarId)
            contentResolver.delete(deleteUri, null, null)
        }
        calendarEventDao.deleteCalendarEvents(school.schoolId, date)
    }
}