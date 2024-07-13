package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTaskDone
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone

data class CHomeworkTask(
    @Embedded val task: DbHomeworkTask,
    @Relation(
        parentColumn = "id",
        entityColumn = "task_id",
        entity = DbHomeworkTaskDone::class
    ) val done: List<CHomeworkTaskDone>
) {
    fun toCoreModel(): HomeworkTaskCore {
        return HomeworkTaskCore(
            id = task.id,
            content = task.content,
            homeworkId = task.homeworkId,
        )
    }

    fun toProfileModel(profile: ClassProfile): HomeworkTaskDone {
        return HomeworkTaskDone(
            id = task.id,
            content = task.content,
            homeworkId = task.homeworkId,
            isDone = done.firstOrNull { it.profile.classProfile.id == profile.id }?.taskDone?.isDone ?: false
        )
    }
}