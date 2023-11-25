package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import es.jvbabi.vplanplus.domain.model.DayDataType

@ProvidedTypeConverter
class DayDataTypeConverter {

    @TypeConverter
    fun dayDataTypeToInt(dayDataType: DayDataType): Int {
        return when (dayDataType) {
            DayDataType.SYNCED -> 0
            DayDataType.NOT_SYNCED -> 1
        }
    }

    @TypeConverter
    fun intToDayDataType(int: Int): DayDataType {
        return when (int) {
            0 -> DayDataType.SYNCED
            1 -> DayDataType.NOT_SYNCED
            else -> throw IllegalArgumentException("Unknown dayDataType")
        }
    }
}