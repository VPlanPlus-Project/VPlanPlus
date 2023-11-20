package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbClass
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.School

data class CClass(
    @Embedded val `class`: DbClass,
    @Relation(
        parentColumn = "schoolClassRefId",
        entityColumn = "schoolId",
        entity = School::class
    ) val school: School
) {
    fun toModel(): Classes {
        return Classes(
            classId = `class`.classId,
            name = `class`.className,
            school = school
        )
    }
}