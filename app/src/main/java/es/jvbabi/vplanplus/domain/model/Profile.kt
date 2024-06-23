package es.jvbabi.vplanplus.domain.model

import java.util.UUID

sealed class Profile(
    val id: UUID,
    val originalName: String,
    val displayName: String,
    val calendarType: ProfileCalendarType,
    val calendarId: Long?
) {
    fun getType(): ProfileType {
        return when (this) {
            is TeacherProfile -> ProfileType.TEACHER
            is RoomProfile -> ProfileType.ROOM
            is ClassProfile -> ProfileType.STUDENT
        }
    }

    abstract fun getSchool(): School
}

class TeacherProfile(
    id: UUID,
    originalName: String,
    displayName: String,
    calendarType: ProfileCalendarType,
    calendarId: Long?,
    val teacher: Teacher
) : Profile(id, originalName, displayName, calendarType, calendarId) {
    override fun getSchool() = teacher.school
}

class RoomProfile(
    id: UUID,
    originalName: String,
    displayName: String,
    calendarType: ProfileCalendarType,
    calendarId: Long?,
    val room: Room
) : Profile(id, originalName, displayName, calendarType, calendarId) {
    override fun getSchool() = room.school
}

class ClassProfile(
    id: UUID,
    originalName: String,
    displayName: String,
    calendarType: ProfileCalendarType,
    calendarId: Long?,
    val group: Group,
    val isHomeworkEnabled: Boolean,
    val defaultLessons: Map<DefaultLesson, Boolean>,
    val vppId: VppId?
) : Profile(id, originalName, displayName, calendarType, calendarId) {
        /**
        * Returns true if the default lesson is enabled for this profile
        * Returns also true if default lesson isn't found in profile
        */
        fun isDefaultLessonEnabled(vpId: Int?): Boolean {
            return defaultLessons.filterKeys { it.vpId == vpId }.values.firstOrNull()?:true
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassProfile

        return id == other.id
    }

    override fun getSchool() = group.school

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + isHomeworkEnabled.hashCode()
        result = 31 * result + defaultLessons.hashCode()
        result = 31 * result + (vppId?.hashCode() ?: 0)
        return result
    }
}

enum class ProfileType {
    TEACHER, STUDENT, ROOM
}

enum class ProfileCalendarType {
    DAY, LESSON, NONE
}