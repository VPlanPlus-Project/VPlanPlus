package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.exam.DbExam
import es.jvbabi.vplanplus.data.model.exam.DbExamReminder
import es.jvbabi.vplanplus.data.model.vppid.DbVppId
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamCategory

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
    ) val defaultLessons: List<CDefaultLesson>,
    @Relation(
        parentColumn = "id",
        entityColumn = "exam_id",
        entity = DbExamReminder::class
    ) val reminders: List<DbExamReminder>
) {

    /**
     * @param contextProfile The profile of the user who is currently viewing the exam, null if it is not relevant. This affects the reminder days.
     */
    fun toModel(contextProfile: ClassProfile?): Exam {
        val type = ExamCategory.of(exam.type)
        if (exam.id < 0) return Exam.Local(
            id = exam.id,
            subject = defaultLessons.firstOrNull { it.`class`.group.id == group.group.id }?.toModel(),
            date = exam.date,
            title = exam.title,
            description = exam.description,
            type = type,
            group = group.toModel(),
            createdAt = exam.createdAt,
            remindDaysBefore = type.remindDaysBefore
        )
        return Exam.Cloud(
            id = exam.id,
            subject = defaultLessons.firstOrNull { it.`class`.group.id == group.group.id }?.toModel(),
            date = exam.date,
            title = exam.title,
            description = exam.description,
            type = type,
            createdBy = createdBy!!.toModel(),
            group = group.toModel(),
            createdAt = exam.createdAt,
            isPublic = exam.isPublic,
            remindDaysBefore = if (exam.useDefaultNotifications) type.remindDaysBefore else reminders
                .filter { it.profileId == (contextProfile?.id ?: it.profileId) }
                .map { it.daysBefore }
                .toSet()
        )
    }
}