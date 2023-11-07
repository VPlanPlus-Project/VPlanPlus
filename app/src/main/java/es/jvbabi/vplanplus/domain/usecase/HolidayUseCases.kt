package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.repository.HolidayRepository

class HolidayUseCases(
    private val holidayRepository: HolidayRepository
) {

    suspend fun getHolidaysBySchoolId(schoolId: Long) =
        holidayRepository.getHolidaysBySchoolId(schoolId)

    suspend fun insertHolidays(holidays: List<Holiday>) = holidayRepository.insertHolidays(holidays)
}