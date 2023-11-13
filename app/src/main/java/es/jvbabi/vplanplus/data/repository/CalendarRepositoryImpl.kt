package es.jvbabi.vplanplus.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.CalendarContract
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

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
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
            )
            val cursor = contentResolver.query(calendarsUri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val calendarId =
                        cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
                    val calendarName =
                        cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                    calendars.add(Calendar(calendarId, calendarName))
                } while (cursor.moveToNext())
                cursor.close()
            }
            emit(calendars)
        }
    }

    override suspend fun getCalendarById(id: Long): Calendar? {
        return getCalendars().firstOrNull()?.firstOrNull { it.id == id }
    }
}