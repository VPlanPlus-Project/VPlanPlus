package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import es.jvbabi.vplanplus.feature.main_grades.domain.model.GradeModifier

@ProvidedTypeConverter
class GradeModifierConverter {
    @TypeConverter
    fun intToModifier(input: Int): GradeModifier {
        return GradeModifier.entries[input]
    }

    @TypeConverter
    fun modifierToInt(modifier: GradeModifier): Int {
        return modifier.ordinal
    }
}