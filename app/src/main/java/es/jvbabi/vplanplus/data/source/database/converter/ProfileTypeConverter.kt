package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import es.jvbabi.vplanplus.domain.model.ProfileType

@ProvidedTypeConverter
class ProfileTypeConverter {

    @TypeConverter
    fun profileTypeToInt(profileType: ProfileType): Int {
        return when (profileType) {
            ProfileType.TEACHER -> 0
            ProfileType.STUDENT -> 1
            ProfileType.ROOM -> 2
        }
    }

    @TypeConverter
    fun intToProfileType(int: Int): ProfileType {
        return when (int) {
            0 -> ProfileType.TEACHER
            1 -> ProfileType.STUDENT
            2 -> ProfileType.ROOM
            else -> throw IllegalArgumentException("Unknown profile type")
        }
    }
}