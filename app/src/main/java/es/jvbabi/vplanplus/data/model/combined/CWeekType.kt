package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbWeekType
import es.jvbabi.vplanplus.domain.model.DbSchool
import es.jvbabi.vplanplus.domain.model.WeekType

data class CWeekType(
    @Embedded val weekType: DbWeekType,
    @Relation(
        parentColumn = "school_id",
        entityColumn = "id",
        entity = DbSchool::class
    ) val school: CSchool
) {
    fun toModel(): WeekType {
        return WeekType(
            id = weekType.id,
            name = weekType.name,
            school = school.toModel()
        )
    }
}