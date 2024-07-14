package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.domain.model.DefaultLesson

data class CDefaultLesson(
    @Embedded val defaultLesson: DbDefaultLesson,
    @Relation(
        parentColumn = "teacher_id",
        entityColumn = "id",
        entity = DbSchoolEntity::class
    ) val teacher: CSchoolEntity?,
    @Relation(
        parentColumn = "class_id",
        entityColumn = "id",
        entity = DbGroup::class
    ) val `class`: CGroup,
) {
    fun toModel(): DefaultLesson {
        return DefaultLesson(
            defaultLessonId = defaultLesson.id,
            vpId = defaultLesson.vpId,
            subject = defaultLesson.subject,
            teacher = teacher?.toTeacherModel(),
            `class` = `class`.toModel(),
        )
    }
}