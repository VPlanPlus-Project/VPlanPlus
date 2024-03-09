package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@ProvidedTypeConverter
class ZonedDateTimeConverter {

    @TypeConverter
    fun timestampToZonedDateTime(ts: Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(ts), ZoneId.of("UTC"))
    }

    @TypeConverter
    fun zonedDateTimeToTimestamp(zonedDateTime: ZonedDateTime): Long {
        return zonedDateTime.toEpochSecond()
    }
}