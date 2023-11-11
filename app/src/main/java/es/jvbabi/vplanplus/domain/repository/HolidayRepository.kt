package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Holiday
import java.time.LocalDate

interface HolidayRepository {

    suspend fun getHolidaysBySchoolId(schoolId: Long): List<Holiday>
    suspend fun getTodayHoliday(schoolId: Long): Holiday?
    suspend fun insertHoliday(holiday: Holiday)
    suspend fun replaceHolidays(holidays: List<Holiday>)
    suspend fun deleteHolidaysBySchoolId(schoolId: Long)
    fun isHoliday(schoolId: Long, date: LocalDate): Boolean
}