package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VppId

data class CVppId(
    @Embedded val vppId: DbVppId,
    @Relation(
        parentColumn = "schoolId",
        entityColumn = "schoolId",
        entity = School::class
    ) val school: School,
    @Relation(
        parentColumn = "classId",
        entityColumn = "id",
        entity = DbSchoolEntity::class
    ) val classes: CSchoolEntity?
) {
    fun toModel(): VppId {
        return VppId(
            id = vppId.id,
            name = vppId.name,
            schoolId = vppId.schoolId,
            school = school,
            className = vppId.className,
            classes = classes?.toClassModel(),
            state = vppId.state,
            email = vppId.email
        )
    }
}