package es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class GetDayUseCase(
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository,
    private val homeworkRepository: HomeworkRepository,
    private val gradeRepository: GradeRepository,
    private val timetableRepository: TimetableRepository
) {
    suspend operator fun invoke(date: LocalDate, profile: Profile) = flow {
        var initial = true
        keyValueRepository.getFlowOrDefault(Keys.LESSON_VERSION_NUMBER, "0").collect { version ->
            val day = planRepository.getDayForProfile(profile, date, version.toLong()).first()
            val lessons = if (day.state == DayDataState.NO_DATA) {
                when (profile) {
                    is ClassProfile -> timetableRepository.getTimetableForGroup(profile.group, date)
                    else -> emptyList()
                }
            } else {
                day.getEnabledLessons(profile)
            }
            if (initial) emit(SchoolDay(date, day.info, lessons, emptyList(), emptyList()))
            initial = false


            val homework = (profile as? ClassProfile)?.let { homeworkRepository.getAllByProfile(it, date).first() } ?: emptyList()
            val grades = (profile as? ClassProfile)?.vppId?.let { gradeRepository.getGradesByUser(it).first().filter { it.givenAt == date } } ?: emptyList()

            SchoolDay(
                date = date,
                info = day.info,
                lessons = lessons,
                homework = homework,
                grades = grades
            ).also { emit(it) }
        }
    }
}