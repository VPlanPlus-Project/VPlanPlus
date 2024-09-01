package es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
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
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(date: LocalDate, profile: Profile) = flow {
        combine(
            listOf(
                keyValueRepository.getFlowOrDefault(Keys.LESSON_VERSION_NUMBER, "0").map { it.toLong() },
                if (profile is ClassProfile) homeworkRepository.getAllByProfile(profile) else emptyFlow<List<PersonalizedHomework>>()
            )
        ) { data ->
            val version = data[0] as Long
            val homework = data[1] as List<PersonalizedHomework>
            val day = planRepository.getDayForProfile(profile, date, version).first()

            SchoolDay(
                date = date,
                info = day.info,
                lessons = day.lessons.filter { (profile is ClassProfile && profile.isDefaultLessonEnabled(it.defaultLesson?.vpId)) || profile !is ClassProfile },
                homework = homework.filter { it.homework.until.toLocalDate() == date }
            )
        }.collect {
            emit(it)
        }
    }
}