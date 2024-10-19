package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalTime

@ProvidedTypeConverter
class LocalTimeConverter {

    @TypeConverter
    fun timestampToLocalTime(ts: Int): LocalTime {
        return LocalTime.ofSecondOfDay(ts.toLong())
    }

    @TypeConverter
    fun localTimeToTimestamp(localTime: LocalTime): Int {
        return localTime.toSecondOfDay()
    }
}