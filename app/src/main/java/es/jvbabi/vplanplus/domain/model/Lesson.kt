package es.jvbabi.vplanplus.domain.model

import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.sha256
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
    val start: LocalDateTime,
    val end: LocalDateTime,
    val vpId: Long?,
    val roomBooking: RoomBooking? = null
) {
    fun toHash(): String {
        return "$lessonNumber${originalSubject ?: ""}${changedSubject ?: ""}${
            teachers.joinToString(
                ""
            )
        }${rooms.joinToString("")}${info ?: ""}".sha256()
    }

    val displaySubject: String
        get() = changedSubject ?: originalSubject ?: "-"

    val subjectIsChanged: Boolean
        get() = changedSubject != null

    /**
     * Calculate the progress of the lesson
     * @param date the date to calculate the progress for
     * @return the progress of the lesson, a value between 0.0 and 1.0
     */
    fun progress(date: LocalDateTime): Float {
        var progress = 0.0
        if (date.toLocalDate().isBefore(LocalDate.now())) progress = 1.0
        if (date.toLocalDate().isAfter(LocalDate.now())) progress = 0.0
        if (date.toLocalDate() == LocalDate.now()) progress = DateUtils.calculateProgress(
            DateUtils.localDateTimeToTimeString(this.start),
            "${date.hour}:${date.minute}",
            DateUtils.localDateTimeToTimeString(this.end)
        ) ?: 0.0
        return progress.toFloat()
    }
}