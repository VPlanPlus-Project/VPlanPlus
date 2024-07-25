package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.School

data class CGroup(
    @Embedded val group: DbGroup,
    @Relation(
        parentColumn = "school_id",
        entityColumn = "id",
        entity = School::class
    ) val school: School
) {
    fun toModel(): Group {
        return Group(
            groupId = group.id,
            name = group.name,
            school = school,
            isClass = group.isClass
        )
    }
}