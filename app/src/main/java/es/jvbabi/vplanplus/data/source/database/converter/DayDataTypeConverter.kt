package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import es.jvbabi.vplanplus.domain.model.DayDataState

@ProvidedTypeConverter
class DayDataTypeConverter {

    @TypeConverter
    fun dayDataTypeToInt(dayDataState: DayDataState): Int {
        return when (dayDataState) {
            DayDataState.DATA -> 0
            DayDataState.NO_DATA -> 1
        }
    }

    @TypeConverter
    fun intToDayDataType(int: Int): DayDataState {
        return when (int) {
            0 -> DayDataState.DATA
            1 -> DayDataState.NO_DATA
            else -> throw IllegalArgumentException("Unknown dayDataType")
        }
    }
}