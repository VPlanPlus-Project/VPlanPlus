package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@ProvidedTypeConverter
class LocalDateConverter {

    @TypeConverter
    fun timestampToLocalDate(ts: Long): LocalDate {
        return ZonedDateTimeConverter().timestampToZonedDateTime(ts).toLocalDate()
    }

    @TypeConverter
    fun localDateToTimestamp(localDate: LocalDate): Long {
        return ZonedDateTimeConverter().zonedDateTimeToTimestamp(ZonedDateTime.of(localDate.atTime(0, 0, 0), ZoneId.of("UTC")))
    }
}