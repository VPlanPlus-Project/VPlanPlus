package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbWeek
import es.jvbabi.vplanplus.data.model.DbWeekType
import es.jvbabi.vplanplus.domain.model.DbSchool
import es.jvbabi.vplanplus.domain.model.Week

data class CWeek(
    @Embedded val week: DbWeek,
    @Relation(
        parentColumn = "week_type_id",
        entityColumn = "id",
        entity = DbWeekType::class
    ) val weekType: CWeekType,
    @Relation(
        parentColumn = "school_id",
        entityColumn = "id",
        entity = DbSchool::class
    ) val school: CSchool
) {
    fun toModel(): Week {
        return Week(
            id = week.id,
            school = school.toModel(),
            weekNumber = week.weekNumber,
            start = week.startDate,
            end = week.endDate,
            type = weekType.toModel()
        )
    }
}