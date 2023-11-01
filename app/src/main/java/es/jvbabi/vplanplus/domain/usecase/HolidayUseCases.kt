package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.repository.HolidayRepository

class HolidayUseCases(
    private val holidayRepository: HolidayRepository
) {

    suspend fun getHolidaysBySchoolId(schoolId: Long) =
        holidayRepository.getHolidaysBySchoolId(schoolId)
}