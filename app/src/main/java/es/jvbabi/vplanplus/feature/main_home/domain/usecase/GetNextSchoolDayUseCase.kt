package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.GetDayUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class GetNextSchoolDayUseCase(
    private val planRepository: PlanRepository,
    private val holidayRepository: HolidayRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val getDayUseCase: GetDayUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<SchoolDay?> {
        return getCurrentProfileUseCase()
            .flatMapLatest { profile ->
                if (profile == null) return@flatMapLatest flowOf(null)
                flow {
                    val dates = planRepository.getLocalPlaDatesForSchool(profile.getSchool().id)
                        .filter { it.isAfter(LocalDate.now()) }
                    val holidays = holidayRepository.getHolidaysBySchoolId(profile.getSchool().id)
                    val date = dates.firstOrNull { date ->
                        date.dayOfWeek.value < profile.getSchool().daysPerWeek &&
                                holidays.none { holiday -> holiday.date.isEqual(date) }
                    } ?: run firstDayAfterHolidays@{
                        val currentHoliday = holidays.firstOrNull { it.date.isEqual(LocalDate.now()) } ?: return@firstDayAfterHolidays null
                        var date = currentHoliday.date
                        while (date.dayOfWeek.value >= profile.getSchool().daysPerWeek || holidays.any { it.date.isEqual(date) }) {
                            date = date.plusDays(1)
                        }
                        return@firstDayAfterHolidays date
                    } ?: return@flow

                    getDayUseCase(date).collect {
                        emit(it)
                    }
                }
            }
    }
}