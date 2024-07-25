package es.jvbabi.vplanplus.domain.model

import java.util.UUID

data class DefaultLesson(
    val defaultLessonId: UUID,
    val vpId: Int,
    val subject: String,
    val teacher: Teacher?,
    val `class`: Group,
) : Comparable<DefaultLesson> {
    override fun compareTo(other: DefaultLesson): Int {
        return this.vpId.compareTo(other.vpId)
    }
}
