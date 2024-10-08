package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.exam.DbExam
import es.jvbabi.vplanplus.data.model.vppid.DbVppId
import es.jvbabi.vplanplus.domain.model.Exam

data class CExam(
    @Embedded val exam: DbExam,
    @Relation(
        parentColumn = "created_by",
        entityColumn = "id",
        entity = DbVppId::class
    ) val createdBy: CVppId?,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = DbGroup::class
    ) val group: CGroup,
    @Relation(
        parentColumn = "subject",
        entityColumn = "vp_id",
        entity = DbDefaultLesson::class
    ) val defaultLessons: List<CDefaultLesson>
) {
    fun toModel(): Exam {
        return Exam(
            id = exam.id,
            subject = defaultLessons.firstOrNull { it.`class`.group.id == group.group.id }?.toModel(),
            date = exam.date,
            title = exam.title,
            description = exam.description,
            type = exam.type,
            createdBy = createdBy?.toModel(),
            group = group.toModel(),
            createdAt = exam.createdAt
        )
    }
}