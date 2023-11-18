package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.util.UUID

@ProvidedTypeConverter
class UuidConverter {

    @TypeConverter
    fun uuidToString(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun stringToUuid(string: String): UUID {
        return UUID.fromString(string)
    }
}