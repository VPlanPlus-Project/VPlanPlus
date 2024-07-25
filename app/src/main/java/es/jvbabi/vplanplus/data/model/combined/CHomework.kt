package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.homework.DbHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkDocument
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkProfileData
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework


data class CHomework(
    @Embedded val homework: DbHomework,
    @Relation(
        parentColumn = "id",
        entityColumn = "homework_id",
        entity = DbHomeworkTask::class
    ) val tasks: List<CHomeworkTask>,
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
    ) val profile: CClassProfile?,
    @Relation(
        parentColumn = "id",
        entityColumn = "homework_id",
        entity = DbHomeworkDocument::class
    ) val documents: List<DbHomeworkDocument>,
    @Relation(
        parentColumn = "id",
        entityColumn = "homework_id",
        entity = DbHomeworkProfileData::class
    ) val profileData: List<DbHomeworkProfileData>
) {
    fun toCoreModel(): HomeworkCore {
        val id = homework.id
        val cratedAt = homework.createdAt
        val until = homework.until
        val defaultLesson = defaultLessons.firstOrNull { it.`class`.group.id == classes.group.id }?.toModel()
        val group = classes.toModel()
        val documents = documents.map { it.toModel() }
        return if (homework.id > 0) {
            HomeworkCore.CloudHomework(
                id = id.toInt(),
                group = group,
                createdAt = cratedAt,
                defaultLesson = defaultLesson,
                until = until,
                tasks = tasks.map { it.toCoreModel() },
                documents = documents,
                createdBy = createdBy!!.toModel(),
                isPublic = homework.isPublic,
            )
        } else {
            HomeworkCore.LocalHomework(
                id = id.toInt(),
                createdAt = cratedAt,
                defaultLesson = defaultLesson,
                until = until,
                tasks = tasks.map { it.toCoreModel() },
                documents = documents,
                profile = profile!!.toModel()
            )
        }
    }

    fun toProfileModel(profile: ClassProfile): PersonalizedHomework {
        return if (homework.id > 0) {
            PersonalizedHomework.CloudHomework(
                profile = profile,
                homework = toCoreModel() as HomeworkCore.CloudHomework,
                tasks = tasks.map { it.toProfileModel(profile) },
                isHidden = profileData.firstOrNull { it.profileId == profile.id }?.isHidden ?: false
            )
        } else {
            PersonalizedHomework.LocalHomework(
                profile = profile,
                homework = toCoreModel() as HomeworkCore.LocalHomework,
                tasks = tasks.map { it.toProfileModel(profile) }
            )
        }
    }
}