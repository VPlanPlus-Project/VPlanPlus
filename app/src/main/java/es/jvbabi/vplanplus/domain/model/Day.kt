package es.jvbabi.vplanplus.domain.model

import es.jvbabi.vplanplus.util.DateUtils.progress
import java.time.LocalDate
import java.time.ZonedDateTime

enum class DayDataState {
    DATA,
    NO_DATA,
}

enum class DayType {
    WEEKEND,
    HOLIDAY,
    NORMAL,
}

data class Day(
    val date: LocalDate,
    val state: DayDataState,
    val type: DayType,
    val lessons: List<Lesson>,
    val info: String?,
) {
    fun getEnabledLessons(profile: Profile) =
        lessons.filter { (profile as? ClassProfile)?.isDefaultLessonEnabled(it.defaultLesson?.vpId) ?: true }

    fun anyLessonsLeft(time: ZonedDateTime, profile: Profile): Boolean {
        return getEnabledLessons(profile).any { time.progress(it.start, it.end) < 1.0 }
    }
}