package es.jvbabi.vplanplus.domain.model

import es.jvbabi.vplanplus.util.sha256
import java.time.ZonedDateTime

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
    val start: ZonedDateTime,
    val end: ZonedDateTime,
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
}