package es.jvbabi.vplanplus.feature.main_calendar.home.domain.model

import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.util.DateUtils.between
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

data class SchoolDay(
    val date: LocalDate,
    val info: String? = null,
    val lessons: List<Lesson>,
    val homework: List<PersonalizedHomework> = emptyList(),
    val grades: List<Grade> = emptyList(),
    val type: DayType = DayType.NORMAL
) {
    constructor(date: LocalDate) : this(date, null, emptyList())

    fun getCurrentLessons(referenceTime: ZonedDateTime = ZonedDateTime.now()): List<Lesson> {
        return lessons.filter { referenceTime.between(it.start, it.end) }
    }

    fun getCurrentOrNextLesson(referenceTime: ZonedDateTime = ZonedDateTime.now()): CurrentOrNextLesson? {
        val currentLessons = getCurrentLessons(referenceTime)
        if (currentLessons.isNotEmpty()) return CurrentOrNextLesson(currentLessons, true)

        val lessonsAfterReferenceTime = lessons
            .associateWith { referenceTime.until(it.end, ChronoUnit.SECONDS) }
            .filter { it.value < 0 }
            .maxByOrNull { it.value }
            .let { it?.key?.lessonNumber ?: -1 }
            .let { previousLessonNumber -> lessons.groupBy { it.lessonNumber }.minByOrNull { it.key }?.value}

        return lessonsAfterReferenceTime?.let { CurrentOrNextLesson(it, false) }
    }
}

data class CurrentOrNextLesson(
    val lessons: List<Lesson>,
    val isCurrent: Boolean
)