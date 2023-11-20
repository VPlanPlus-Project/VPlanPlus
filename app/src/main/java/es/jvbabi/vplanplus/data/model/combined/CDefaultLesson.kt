package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbClass
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbTeacher
import es.jvbabi.vplanplus.domain.model.DefaultLesson

data class CDefaultLesson(
    @Embedded val defaultLesson: DbDefaultLesson,
    @Relation(
        parentColumn = "teacherId",
        entityColumn = "teacherId",
        entity = DbTeacher::class
    ) val teacher: CTeacher?,
    @Relation(
        parentColumn = "classId",
        entityColumn = "classId",
        entity = DbClass::class
    ) val `class`: CClass,
) {
    fun toModel(): DefaultLesson {
        return DefaultLesson(
            defaultLessonId = defaultLesson.defaultLessonId,
            vpId = defaultLesson.vpId,
            subject = defaultLesson.subject,
            teacher = teacher?.toModel(),
            `class` = `class`.toModel(),
        )
    }
}