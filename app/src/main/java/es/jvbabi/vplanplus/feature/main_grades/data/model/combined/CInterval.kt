package es.jvbabi.vplanplus.feature.main_grades.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbInterval
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbYear
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Interval
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Year

data class CInterval(
    @Embedded val interval: DbInterval,
    @Relation(
        parentColumn = "year_id",
        entityColumn = "id"
    ) val year: DbYear
) {
    fun toModel(): Pair<Interval, Year> {
        return Interval(
            id = interval.id,
            name = interval.name,
            type = interval.type,
            from = interval.from,
            to = interval.to,
            includedIntervalId = interval.includedIntervalId
        ) to year.toModel()
    }
}