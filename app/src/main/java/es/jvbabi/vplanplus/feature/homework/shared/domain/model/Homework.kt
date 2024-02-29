package es.jvbabi.vplanplus.feature.homework.shared.domain.model

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.util.sha256
import java.time.LocalDate
import java.time.LocalDateTime

open class Homework(
    val id: Long,
    val createdBy: VppId?,
    val classes: Classes,
    val createdAt: LocalDateTime,
    val defaultLesson: DefaultLesson,
    val isPublic: Boolean,
    val until: LocalDate,
    val tasks: List<HomeworkTask>,
) {
    fun buildHash(): String {
        return "$id$createdBy$createdAt${defaultLesson.vpId}$until$isPublic$classes${tasks.joinToString { it.content }}".sha256()
    }
}

data class HomeworkTask(
    val id: Long,
    val individualId: Long?,
    val content: String,
    val done: Boolean,
)