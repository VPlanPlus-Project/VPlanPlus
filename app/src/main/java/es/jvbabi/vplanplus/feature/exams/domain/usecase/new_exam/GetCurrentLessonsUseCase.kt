package es.jvbabi.vplanplus.feature.exams.domain.usecase.new_exam

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.GetDayUseCase
import es.jvbabi.vplanplus.util.DateUtils.between
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZonedDateTime

class GetCurrentLessonsUseCase(
    private val getDayUseCase: GetDayUseCase,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(): Set<DefaultLesson> {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return emptySet()
        val day = getDayUseCase(date = LocalDate.now()).first()
        return day.lessons
            .filterIsInstance<Lesson.SubstitutionPlanLesson>()
            .filter { profile.isDefaultLessonEnabled(it.defaultLesson?.vpId) && ZonedDateTime.now().between(it.start, it.end) }
            .mapNotNull { it.defaultLesson }
            .toSet()
    }
}