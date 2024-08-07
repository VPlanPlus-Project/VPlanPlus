package es.jvbabi.vplanplus.domain.model

import es.jvbabi.vplanplus.util.sha256
import java.time.ZonedDateTime

data class Lesson(
    val `class`: Group,
    val lessonNumber: Int,
    val originalSubject: String?,
    val changedSubject: String?,
    val teachers: List<Teacher>,
    val teacherIsChanged: Boolean,
    val rooms: List<Room>,
    val roomIsChanged: Boolean,
    val info: String?,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val defaultLesson: DefaultLesson?,
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
}