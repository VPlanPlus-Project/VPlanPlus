package es.jvbabi.vplanplus.feature.main_homework.shared.domain.model

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.VppId
import java.time.ZonedDateTime

open class Homework(
    val id: Long,
    val createdBy: VppId?,
    val classes: Classes,
    val createdAt: ZonedDateTime,
    val defaultLesson: DefaultLesson?,
    val isPublic: Boolean,
    val until: ZonedDateTime,
    val tasks: List<HomeworkTask>,
    val isHidden: Boolean
)

data class HomeworkTask(
    val id: Long,
    val content: String,
    val done: Boolean,
)