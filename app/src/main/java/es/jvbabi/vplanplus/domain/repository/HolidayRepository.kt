package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Holiday
import java.time.LocalDate

interface HolidayRepository {

    suspend fun getHolidaysBySchoolId(schoolId: Int): List<Holiday>
    suspend fun getTodayHoliday(schoolId: Int): Holiday?
    suspend fun insertHoliday(schoolId: Int?, date: LocalDate)
    suspend fun deleteHolidaysBySchoolId(schoolId: Int)
    suspend fun isHoliday(schoolId: Int, date: LocalDate): Boolean
    suspend fun getDayType(schoolId: Int, date: LocalDate): DayType
}