package es.jvbabi.vplanplus.domain.model

import java.util.UUID

data class DefaultLesson(
    val defaultLessonId: UUID,
    val vpId: Long,
    val subject: String,
    val teacher: Teacher?,
    val `class`: Classes,
) : Comparable<DefaultLesson> {
    override fun compareTo(other: DefaultLesson): Int {
        return this.vpId.compareTo(other.vpId)
    }
}
