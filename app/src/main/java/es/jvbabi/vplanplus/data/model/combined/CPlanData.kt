package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbPlanData
import es.jvbabi.vplanplus.domain.model.School

data class CPlanData(
    @Embedded val planData: DbPlanData,
    @Relation(
        parentColumn = "schoolId",
        entityColumn = "id",
        entity = School::class
    ) val school: School
)
