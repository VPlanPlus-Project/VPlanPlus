package es.jvbabi.vplanplus.domain.model

import es.jvbabi.vplanplus.util.sha256
import java.time.ZonedDateTime
import java.util.UUID

sealed class Lesson(
    val group: Group,
    val subject: String,
    val lessonNumber: Int,
    val rooms: List<Room>,
    val teachers: List<Teacher>,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val week: Week?,
    val weekType: WeekType?
) {
    class TimetableLesson(
        val id: UUID,
        group: Group,
        subject: String,
        lessonNumber: Int,
        rooms: List<Room>,
        teachers: List<Teacher>,
        start: ZonedDateTime,
        end: ZonedDateTime,
        week: Week?,
        weekType: WeekType?
    ) : Lesson(
        group,
        subject,
        lessonNumber,
        rooms,
        teachers,
        start,
        end,
        week,
        weekType
    ) {
        override fun toHash(): String {
            return "$lessonNumber$subject${teachers.joinToString("")}${rooms.joinToString("")}".sha256()
        }

        override val displaySubject: String
            get() = subject

        fun copy(
            subject: String = this.subject,
            rooms: List<Room> = this.rooms,
            teachers: List<Teacher> = this.teachers
        ): TimetableLesson {
            return TimetableLesson(
                id,
                group,
                subject,
                lessonNumber,
                rooms,
                teachers,
                start,
                end,
                week,
                weekType
            )
        }
    }

    class SubstitutionPlanLesson(
        val changedSubject: String?,
        val teacherIsChanged: Boolean,
        val roomIsChanged: Boolean,
        val info: String?,
        val defaultLesson: DefaultLesson?,
        val roomBooking: RoomBooking?,
        val id: UUID,
        group: Group,
        subject: String,
        lessonNumber: Int,
        rooms: List<Room>,
        teachers: List<Teacher>,
        start: ZonedDateTime,
        end: ZonedDateTime,
        week: Week?,
        weekType: WeekType?
    ) : Lesson(
        group,
        subject,
        lessonNumber,
        rooms,
        teachers,
        start,
        end,
        week,
        weekType
    ) {
        override fun toHash(): String {
            return "$lessonNumber${subject}${changedSubject ?: ""}${
                teachers.joinToString(
                    ""
                )
            }${rooms.joinToString("")}${info ?: ""}".sha256()
        }

        override val displaySubject: String
            get() = changedSubject ?: subject

        fun copy(
            changedSubject: String? = this.changedSubject,
            teacherIsChanged: Boolean = this.teacherIsChanged,
            roomIsChanged: Boolean = this.roomIsChanged,
            info: String? = this.info,
            defaultLesson: DefaultLesson? = this.defaultLesson,
            roomBooking: RoomBooking? = this.roomBooking
        ): SubstitutionPlanLesson {
            return SubstitutionPlanLesson(
                changedSubject,
                teacherIsChanged,
                roomIsChanged,
                info,
                defaultLesson,
                roomBooking,
                id,
                group,
                subject,
                lessonNumber,
                rooms,
                teachers,
                start,
                end,
                week,
                weekType
            )
        }
    }

    abstract fun toHash(): String
    abstract val displaySubject: String
}