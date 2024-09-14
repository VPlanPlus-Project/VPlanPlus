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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetDayUseCase(
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository,
    private val homeworkRepository: HomeworkRepository,
    private val gradeRepository: GradeRepository,
    private val timetableRepository: TimetableRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(date: LocalDate, profile: Profile): Flow<SchoolDay> {
        var schoolDay = SchoolDay(date)
        return keyValueRepository.getFlowOrDefault(Keys.LESSON_VERSION_NUMBER, "0").flatMapLatest { version ->
            flow {
                val day = planRepository.getDayForProfile(profile, date, version.toLong()).first()
                val lessons = if (day.state == DayDataState.NO_DATA) {
                    when (profile) {
                        is ClassProfile -> timetableRepository.getTimetableForGroup(profile.group, date)
                        else -> emptyList()
                    }
                } else {
                    day.getEnabledLessons(profile)
                }
                schoolDay = schoolDay.copy(lessons = lessons, info = day.info)
                emit(schoolDay)
                
                val homeworkFlow = (profile as? ClassProfile)?.let { homeworkRepository.getAllByProfile(it, date) } ?: emptyFlow()
                val gradesFlow = (profile as? ClassProfile)?.vppId?.let { gradeRepository.getGradesByUser(it).map { grades -> grades.filter { grade -> grade.givenAt == date } } } ?: emptyFlow()
                combine(homeworkFlow, gradesFlow) { homework, grades ->
                    schoolDay.copy(homework = homework, grades = grades)
                }.collect {
                    schoolDay = it
                    emit(schoolDay)
                }
            }
        }
    }
}