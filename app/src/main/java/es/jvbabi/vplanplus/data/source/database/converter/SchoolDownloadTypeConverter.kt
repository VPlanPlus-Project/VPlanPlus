package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode

@ProvidedTypeConverter
class SchoolDownloadTypeConverter {

    @TypeConverter
    fun schoolDownloadModeToInt(schoolDownloadMode: SchoolDownloadMode): String {
        return schoolDownloadMode.name
    }

    @TypeConverter
    fun intToSchoolDownloadMode(string: String): SchoolDownloadMode {
        return SchoolDownloadMode.valueOf(string)
    }
}