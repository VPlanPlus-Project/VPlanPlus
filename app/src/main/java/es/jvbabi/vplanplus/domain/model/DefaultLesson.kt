package es.jvbabi.vplanplus.domain.model

import java.util.UUID

data class DefaultLesson(
    val defaultLessonId: UUID,
    val vpId: Int,
    val subject: String,
    val teacher: Teacher?,
    val `class`: Group,
    val courseGroup: String?
)