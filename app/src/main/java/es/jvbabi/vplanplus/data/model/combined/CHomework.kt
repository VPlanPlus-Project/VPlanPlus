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
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.LocalHomework


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
        val id = homework.id
        val cratedAt = homework.createdAt
        val until = homework.until
        val defaultLesson = defaultLessons.firstOrNull { it.`class`.group.id == classes.group.id }?.toModel()
        val group = classes.toModel()
        val documents = documents.map { it.toModel(context) }
        return if (homework.id > 0) {
            CloudHomework(
                id = id,
                group = group,
                createdAt = cratedAt,
                defaultLesson = defaultLesson,
                until = until,
                tasks = tasks.map { it.toModel() },
                documents = documents,
                createdBy = createdBy!!.toModel(),
                isPublic = homework.isPublic,
                isHidden = homework.isHidden
            )
        } else {
            LocalHomework(
                id = id,
                group = group,
                createdAt = cratedAt,
                defaultLesson = defaultLesson,
                until = until,
                tasks = tasks.map { it.toModel() },
                documents = documents,
                profile = profile.toModel()
            )
        }
    }
}