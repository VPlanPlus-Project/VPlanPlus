package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Holiday

interface HolidayRepository {

    suspend fun getHolidaysBySchoolId(schoolId: String): List<Holiday>
    suspend fun getTodayHoliday(schoolId: String): Holiday?
    suspend fun insertHoliday(holiday: Holiday)
    suspend fun insertHolidays(holidays: List<Holiday>)
    suspend fun deleteHolidaysBySchoolId(schoolId: String)
}