package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbPlanData
import es.jvbabi.vplanplus.domain.model.Plan
import es.jvbabi.vplanplus.domain.model.School

data class CPlanData(
    @Embedded val planData: DbPlanData,
    @Relation(
        parentColumn = "schoolId",
        entityColumn = "schoolId",
        entity = School::class
    ) val school: School
) {
    fun toModel(): Plan {
        return Plan(
            school = school,
            createAt = planData.createDate,
            date = planData.planDate,
            info = planData.info,
            version = planData.version
        )
    }
}