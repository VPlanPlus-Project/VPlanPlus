package es.jvbabi.vplanplus.feature.main_homework.shared.domain.model

import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.util.sha256
import java.time.ZonedDateTime

open class Homework(
    val id: Long,
    val createdBy: VppId?,
    val classes: Classes,
    val createdAt: ZonedDateTime,
    val defaultLesson: DefaultLesson,
    val isPublic: Boolean,
    val until: ZonedDateTime,
    val tasks: List<HomeworkTask>,
    val isHidden: Boolean
) {
    fun buildHash(): String {
        val converter = ZonedDateTimeConverter()
        return "$id${createdBy?.id}${converter.zonedDateTimeToTimestamp(createdAt)}${defaultLesson.vpId}${converter.zonedDateTimeToTimestamp(until)}$isPublic${classes.name}${tasks.joinToString { it.content }}".sha256().lowercase()
    }
}

data class HomeworkTask(
    val id: Long,
    val content: String,
    val done: Boolean,
)