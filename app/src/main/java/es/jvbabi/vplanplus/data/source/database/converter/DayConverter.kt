package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate

@ProvidedTypeConverter
class DayConverter {

    @TypeConverter
    fun timestampToLocalDate(ts: Long): LocalDate {
        return DateUtils.getDateFromTimestamp(ts)
    }

    @TypeConverter
    fun localDateToTimestamp(localDate: LocalDate): Long {
        return DateUtils.getDayTimestamp(localDate)
    }
}