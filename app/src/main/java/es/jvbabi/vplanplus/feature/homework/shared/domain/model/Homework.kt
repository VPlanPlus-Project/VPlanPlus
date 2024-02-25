package es.jvbabi.vplanplus.feature.homework.shared.domain.model

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.VppId
import java.time.LocalDate

data class Homework(
    val id: Int,
    val createdBy: VppId?,
    val classes: Classes,
    val createdAt: LocalDate,
    val defaultLesson: DefaultLesson,
    val until: LocalDate,
    val tasks: List<HomeworkTask>,
)

data class HomeworkTask(
    val id: Int,
    val content: String,
    val done: Boolean,
)