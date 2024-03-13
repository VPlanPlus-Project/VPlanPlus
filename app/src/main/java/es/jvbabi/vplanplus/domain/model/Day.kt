package es.jvbabi.vplanplus.domain.model

import java.time.LocalDate

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
    fun getFilteredLessons(profile: Profile) =
        lessons.filter { profile.isDefaultLessonEnabled(it.vpId) }
}