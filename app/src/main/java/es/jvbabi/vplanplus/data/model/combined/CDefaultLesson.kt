package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.domain.model.DefaultLesson

data class CDefaultLesson(
    @Embedded val defaultLesson: DbDefaultLesson,
    @Relation(
        parentColumn = "teacherId",
        entityColumn = "teacherId",
        entity = DbSchoolEntity::class
    ) val teacher: CSchoolEntity?,
    @Relation(
        parentColumn = "classId",
        entityColumn = "classId",
        entity = DbSchoolEntity::class
    ) val `class`: CSchoolEntity,
) {
    fun toModel(): DefaultLesson {
        return DefaultLesson(
            defaultLessonId = defaultLesson.defaultLessonId,
            vpId = defaultLesson.vpId,
            subject = defaultLesson.subject,
            teacher = teacher?.toTeacherModel(),
            `class` = `class`.toClassModel(),
        )
    }
}