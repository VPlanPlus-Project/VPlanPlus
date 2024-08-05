package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbPlanData
import es.jvbabi.vplanplus.domain.model.DbSchool
import es.jvbabi.vplanplus.domain.model.Plan

data class CPlanData(
    @Embedded val planData: DbPlanData,
    @Relation(
        parentColumn = "school_id",
        entityColumn = "id",
        entity = DbSchool::class
    ) val school: CSchool
) {
    fun toModel(): Plan {
        return Plan(
            school = school.toModel(),
            createAt = planData.createDate,
            date = planData.planDate,
            info = planData.info,
            version = planData.version
        )
    }
}