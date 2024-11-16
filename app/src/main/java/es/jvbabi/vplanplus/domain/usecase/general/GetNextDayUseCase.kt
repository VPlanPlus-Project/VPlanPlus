package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class GetNextDayUseCase(
    private val planRepository: PlanRepository,
    private val holidayRepository: HolidayRepository,
    private val getDayUseCase: GetDayUseCase
) {

    /**
     * @param fast If true, it will load the lessons, emit them and then load the exams and homeworks. If false, it will wait until all the data is loaded before emitting the school day.
     */
    suspend operator fun invoke(profile: Profile, fast: Boolean = true) = flow {
        val dates = planRepository.getLocalPlaDatesForSchool(profile.getSchool().id)
            .filter { it.isAfter(LocalDate.now()) }
        val holidays = holidayRepository.getHolidaysBySchoolId(profile.getSchool().id)
        val date = dates.firstOrNull { date ->
            date.dayOfWeek.value <= profile.getSchool().daysPerWeek &&
                    holidays.none { holiday -> holiday.date.isEqual(date) }
        } ?: run firstDayAfterHolidays@{
            val currentHoliday = holidays.firstOrNull { it.date.isEqual(LocalDate.now()) }
                ?: return@firstDayAfterHolidays null
            var date = currentHoliday.date
            while (date.dayOfWeek.value >= profile.getSchool().daysPerWeek || holidays.any {
                    it.date.isEqual(
                        date
                    )
                }) {
                date = date.plusDays(1)
            }
            return@firstDayAfterHolidays date
        } ?: return@flow

        getDayUseCase(date, profile, fast).collect {
            emit(it)
        }
    }
}