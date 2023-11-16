package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbTeacher
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher

data class CTeacher(
    @Embedded val teacher: DbTeacher,
    @Relation(
        parentColumn = "schoolTeacherRefId",
        entityColumn = "schoolId",
        entity = School::class
    ) val school: School
) {
    fun toModel(): Teacher {
        return Teacher(
            teacherId = teacher.teacherId,
            acronym = teacher.acronym,
            school = school
        )
    }
}