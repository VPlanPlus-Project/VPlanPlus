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
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
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
    suspend operator fun invoke(date: LocalDate, profile: Profile) = flow<SchoolDay> {
        combine(
            listOf(
                keyValueRepository.getFlowOrDefault(Keys.LESSON_VERSION_NUMBER, "0").map { it.toLong() },
                if (profile is ClassProfile) homeworkRepository.getAllByProfile(profile, date) else emptyFlow<List<PersonalizedHomework>>(),
            )
        ) { data ->
            val version = data[0] as Long
            val homework = data[1] as List<PersonalizedHomework>
            val day = planRepository.getDayForProfile(profile, date, version).first()
            val lessons = if (day.state == DayDataState.NO_DATA) {
                when (profile) {
                    is ClassProfile -> timetableRepository.getTimetableForGroup(profile.group, date)
                    else -> emptyList()
                }
            } else {
                day.getEnabledLessons(profile)
            }

            SchoolDay(
                date = date,
                info = day.info,
                lessons = lessons,
                homework = homework,
                grades = if (profile is ClassProfile && profile.vppId != null) gradeRepository.getGradesByUser(profile.vppId).first().filter { it.givenAt == date } else emptyList()
            )
        }.collect {
            emit(it)
        }
    }
}