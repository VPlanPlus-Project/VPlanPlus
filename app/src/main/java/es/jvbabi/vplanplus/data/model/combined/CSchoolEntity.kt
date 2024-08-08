package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.domain.model.DbSchool
import es.jvbabi.vplanplus.domain.model.Teacher

data class CSchoolEntity(
    @Embedded val schoolEntity: DbSchoolEntity,
    @Relation(
        parentColumn = "school_id",
        entityColumn = "id",
        entity = DbSchool::class
    ) val school: CSchool
) {

    fun toTeacherModel(): Teacher {
        return Teacher(
            teacherId = schoolEntity.id,
            acronym = schoolEntity.name,
            school = school.toModel()
        )
    }
}