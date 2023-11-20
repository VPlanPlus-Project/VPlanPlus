package es.jvbabi.vplanplus.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Lesson(
    val `class`: Classes,
    val lessonNumber: Int,
    val originalSubject: String?,
    val changedSubject: String?,
    val teachers: List<String>,
    val teacherIsChanged: Boolean,
    val rooms: List<String>,
    val roomIsChanged: Boolean,
    val info: String?,
    val day: LocalDate,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val vpId: Long?
) {
    val displaySubject: String
        get() = changedSubject ?: originalSubject ?: "-"

    val subjectIsChanged: Boolean
        get() = changedSubject != null
}