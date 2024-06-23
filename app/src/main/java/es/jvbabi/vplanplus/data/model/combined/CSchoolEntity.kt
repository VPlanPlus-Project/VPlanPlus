package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher

data class CSchoolEntity(
    @Embedded val schoolEntity: DbSchoolEntity,
    @Relation(
        parentColumn = "school_id",
        entityColumn = "id",
        entity = School::class
    ) val school: School
) {

    fun toTeacherModel(): Teacher {
        return Teacher(
            teacherId = schoolEntity.id,
            acronym = schoolEntity.name,
            school = school
        )
    }

    fun toRoomModel(): Room {
        return Room(
            roomId = schoolEntity.id,
            name = schoolEntity.name,
            school = school
        )
    }
}