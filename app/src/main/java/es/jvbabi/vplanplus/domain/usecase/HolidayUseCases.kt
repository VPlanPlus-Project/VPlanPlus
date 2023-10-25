package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.util.DateUtils

class HolidayUseCases(
    private val holidayRepository: HolidayRepository
) {

    suspend fun getHolidaysBySchoolId(schoolId: String) =
        holidayRepository.getHolidaysBySchoolId(schoolId)

    suspend fun getTodayHoliday(schoolId: String) = holidayRepository.getTodayHoliday(schoolId)
    suspend fun insertHoliday(holiday: Holiday) = holidayRepository.insertHoliday(holiday)
    suspend fun insertHoliday(day: Int, month: Int, year: Int, schoolId: String) {
        holidayRepository.insertHoliday(
            Holiday(
                schoolId = schoolId,
                timestamp = DateUtils.getDayTimestamp(year = year, month = month, day = day),
            )
        )
    }

    suspend fun getHolidaysBySchoolIdOnline(
        schoolId: String,
        username: String,
        password: String
    ): OnlineResponse<List<Holiday>> {
        return holidayRepository.getHolidaysBySchoolIdOnline(
            schoolId = schoolId,
            username = username,
            password = password
        )
    }

    suspend fun deleteHolidaysBySchoolId(schoolId: String) =
        holidayRepository.deleteHolidaysBySchoolId(schoolId)
}