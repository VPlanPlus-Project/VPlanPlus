package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class GetNextSchoolDayUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val planRepository: PlanRepository,
    private val homeworkRepository: HomeworkRepository,
    private val timetableRepository: TimetableRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<SchoolDay?> {
        return combine(
            keyValueRepository.getFlowOrDefault(Keys.LESSON_VERSION_NUMBER, "0"),
            getCurrentProfileUseCase()
        ) { version, profile ->
            return@combine version to profile
        }.flatMapLatest { (version, profile) ->
            if (profile == null) return@flatMapLatest flowOf(null)
            var schoolDay: SchoolDay?
            flow {
                var startDate = LocalDate.now()
                var day: Day?
                while (true) {
                    startDate = startDate.plusDays(1)
                    day = planRepository.getDayForProfile(profile, startDate, version.toLong()).first()
                    if (day.type == DayType.NORMAL) {
                        break
                    }
                }

                if (day == null) {
                    Log.e("GetNextSchoolDayUseCase", "No day found for profile ${profile.id}")
                    return@flow
                }

                val lessons = if (day.state == DayDataState.NO_DATA) {
                    when (profile) {
                        is ClassProfile -> timetableRepository.getTimetableForGroup(
                            profile.group,
                            startDate
                        )
                        else -> emptyList()
                    }
                } else {
                    day.getEnabledLessons(profile)
                }

                schoolDay = SchoolDay(
                    date = startDate,
                    lessons = lessons,
                    info = day.info,
                    type = day.type
                )
                val homeworkFlow =
                    (profile as? ClassProfile)?.let { homeworkRepository.getAllByProfile(it) }
                        ?: flow { emit(emptyList()) }
                homeworkFlow.collect { homework->
                    schoolDay = schoolDay!!.copy(
                        homework = homework
                            .filter { it is PersonalizedHomework.LocalHomework || (it is PersonalizedHomework.CloudHomework && !it.isHidden) }
                            .filter {
                                it.homework.until.toLocalDate() == startDate || (!it.allDone() && it.homework.until.toLocalDate()
                                    .isBefore(startDate))
                            }
                            .sortedBy { "${it.homework.until.toEpochSecond()}__${it.homework.defaultLesson?.subject}" }
                    )
                    emit(schoolDay)
                }
            }
        }
    }
}