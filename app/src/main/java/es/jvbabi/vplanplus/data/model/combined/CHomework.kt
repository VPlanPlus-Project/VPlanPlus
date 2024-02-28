package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbHomework
import es.jvbabi.vplanplus.data.model.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework


data class CHomework(
    @Embedded val homework: DbHomework,
    @Relation(
        parentColumn = "id",
        entityColumn = "homeworkId",
        entity = DbHomeworkTask::class
    ) val tasks: List<DbHomeworkTask>,
    @Relation(
        parentColumn = "defaultLessonVpId",
        entityColumn = "vpId",
        entity = DbDefaultLesson::class
    ) val defaultLesson: CDefaultLesson,
    @Relation(
        parentColumn = "createdBy",
        entityColumn = "id",
        entity = DbVppId::class
    ) val createdBy: CVppId?,
    @Relation(
        parentColumn = "classes",
        entityColumn = "id",
        entity = DbSchoolEntity::class
    ) val classes: CSchoolEntity
) {
    fun toModel(): Homework {
        return Homework(
            id = homework.id,
            createdBy = createdBy?.toModel(),
            createdAt = homework.createdAt,
            defaultLesson = defaultLesson.toModel(),
            until = homework.until,
            tasks = tasks.map { it.toModel() },
            classes = classes.toClassModel(),
            isPublic = homework.isPublic
        )
    }
}