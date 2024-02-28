package es.jvbabi.vplanplus.feature.homework.shared.domain.model

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.VppId
import java.time.LocalDate
import java.time.LocalDateTime

data class Homework(
    val id: Long,
    val createdBy: VppId?,
    val classes: Classes,
    val createdAt: LocalDateTime,
    val defaultLesson: DefaultLesson,
    val isPublic: Boolean,
    val until: LocalDate,
    val tasks: List<HomeworkTask>,
)

data class HomeworkTask(
    val id: Long,
    val individualId: Long?,
    val content: String,
    val done: Boolean,
)