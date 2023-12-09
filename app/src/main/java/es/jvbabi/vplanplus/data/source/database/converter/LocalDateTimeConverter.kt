package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

@ProvidedTypeConverter
class LocalDateTimeConverter {

    @TypeConverter
    fun localDateTimeToTimestamp(localDateTime: LocalDateTime): Long {
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    @TypeConverter
    fun timestampToLocalDateTime(ts: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts*1000), TimeZone.getDefault().toZoneId())
    }
}