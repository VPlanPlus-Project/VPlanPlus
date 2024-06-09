package es.jvbabi.vplanplus.data.model.combined

import android.content.Context
import android.net.Uri
import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.homework.DbHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import java.io.File


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
    ) val defaultLessons: List<CDefaultLesson>,
    @Relation(
        parentColumn = "createdBy",
        entityColumn = "id",
        entity = DbVppId::class
    ) val createdBy: CVppId?,
    @Relation(
        parentColumn = "classes",
        entityColumn = "id",
        entity = DbSchoolEntity::class
    ) val classes: CSchoolEntity,
    @Relation(
        parentColumn = "profile_id",
        entityColumn = "profileId",
        entity = DbProfile::class
    ) val profile: CProfile,
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
            defaultLesson = defaultLessons.firstOrNull { it.`class`.schoolEntity.id == classes.schoolEntity.id }?.toModel(),
            until = homework.until,
            tasks = tasks.map { it.toModel() },
            classes = classes.toClassModel(),
            isPublic = homework.isPublic,
            isHidden = homework.hidden,
            profile = profile.toModel(),
            documents = documents.map { Uri.fromFile(File(context.filesDir, "homework_documents/${it.id}.pdf")) }
        )
    }
}