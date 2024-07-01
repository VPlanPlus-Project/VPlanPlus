package es.jvbabi.vplanplus.data.model.combined

import android.content.Context
import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.homework.DbHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkDocument
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework


data class CHomework(
    @Embedded val homework: DbHomework,
    @Relation(
        parentColumn = "id",
        entityColumn = "homework_id",
        entity = DbHomeworkTask::class
    ) val tasks: List<DbHomeworkTask>,
    @Relation(
        parentColumn = "default_lesson_vp_id",
        entityColumn = "vp_id",
        entity = DbDefaultLesson::class
    ) val defaultLessons: List<CDefaultLesson>,
    @Relation(
        parentColumn = "created_by",
        entityColumn = "id",
        entity = DbVppId::class
    ) val createdBy: CVppId?,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = DbGroup::class
    ) val classes: CGroup,
    @Relation(
        parentColumn = "profile_id",
        entityColumn = "id",
        entity = DbClassProfile::class
    ) val profile: CClassProfile,
    @Relation(
        parentColumn = "id",
        entityColumn = "homework_id",
        entity = DbHomeworkDocument::class
    ) val documents: List<DbHomeworkDocument>
) {
    fun toModel(context: Context): Homework {
        return Homework(
            id = homework.id,
            createdBy = createdBy?.toModel(),
            createdAt = homework.createdAt,
            defaultLesson = defaultLessons.firstOrNull { it.`class`.group.id == classes.group.id }?.toModel(),
            until = homework.until,
            tasks = tasks.map { it.toModel() },
            group = classes.toModel(),
            isPublic = homework.isPublic,
            isHidden = homework.isHidden,
            profile = profile.toModel(),
            documents = documents.map { it.toModel(context) }
        )
    }
}