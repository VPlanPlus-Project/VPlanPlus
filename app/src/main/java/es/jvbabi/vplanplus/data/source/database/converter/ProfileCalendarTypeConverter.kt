package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import es.jvbabi.vplanplus.data.model.ProfileCalendarType

@ProvidedTypeConverter
class ProfileCalendarTypeConverter {

    @TypeConverter
    fun profileCalendarTypeToInt(type: ProfileCalendarType): Int {
        return when (type) {
            ProfileCalendarType.DAY -> 0
            ProfileCalendarType.LESSON -> 1
            ProfileCalendarType.NONE -> 2
        }
    }

    @TypeConverter
    fun intToProfileCalendarType(int: Int): ProfileCalendarType {
        return when (int) {
            0 -> ProfileCalendarType.DAY
            1 -> ProfileCalendarType.LESSON
            2 -> ProfileCalendarType.NONE
            else -> throw IllegalArgumentException("Unknown ProfileCalendarType")
        }
    }
}