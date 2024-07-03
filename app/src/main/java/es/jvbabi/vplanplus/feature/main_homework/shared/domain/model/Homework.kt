package es.jvbabi.vplanplus.feature.main_homework.shared.domain.model

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.VppId
import java.time.LocalDate
import java.time.ZonedDateTime

abstract class Homework(
    val id: Long,
    val group: Group,
    val createdAt: ZonedDateTime,
    val defaultLesson: DefaultLesson?,
    val until: ZonedDateTime,
    val tasks: List<HomeworkTask>,
    val documents: List<HomeworkDocument>
) {
    fun isOverdue(date: LocalDate): Boolean {
        return until.toLocalDate().isBefore(date) && tasks.any { !it.isDone }
    }

    fun getTaskById(id: Long): HomeworkTask? {
        return tasks.find { it.id == id.toInt() }
    }
}

class CloudHomework(
    id: Long,
    group: Group,
    createdAt: ZonedDateTime,
    defaultLesson: DefaultLesson?,
    val isPublic: Boolean,
    until: ZonedDateTime,
    tasks: List<HomeworkTask>,
    val isHidden: Boolean,
    documents: List<HomeworkDocument>,
    val createdBy: VppId
) : Homework(id, group, createdAt, defaultLesson, until, tasks, documents) {
    init {
        require(id > 0)
    }
}

class LocalHomework(
    id: Long,
    group: Group,
    createdAt: ZonedDateTime,
    defaultLesson: DefaultLesson?,
    until: ZonedDateTime,
    tasks: List<HomeworkTask>,
    documents: List<HomeworkDocument>,
    val profile: Profile
) : Homework(id, group, createdAt, defaultLesson, until, tasks, documents) {
    init {
        require(id < 0)
        require(profile is ClassProfile)
    }
}

data class HomeworkTask(
    val id: Int,
    val content: String,
    val isDone: Boolean,
    val homeworkId: Int
)