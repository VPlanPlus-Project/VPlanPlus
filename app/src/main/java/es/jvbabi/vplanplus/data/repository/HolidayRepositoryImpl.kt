package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.HolidayDao
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.util.DateUtils

class HolidayRepositoryImpl(
    private val holidayDao: HolidayDao
) : HolidayRepository {
    override suspend fun getHolidaysBySchoolId(schoolId: String): List<Holiday> {
        return holidayDao.getHolidaysBySchoolId(schoolId)
    }

    override suspend fun getTodayHoliday(schoolId: String): Holiday? {
        return holidayDao.getHolidaysBySchoolId(schoolId).find {
            it.timestamp == DateUtils.getCurrentDayTimestamp()
        }
    }

    override suspend fun insertHolidays(holidays: List<Holiday>) {
        holidays.map { it.schoolId }.toSet().forEach {
            holidayDao.deleteHolidaysBySchoolId(it?:"")
        }
        holidays.forEach {
            holidayDao.insertHoliday(it)
        }
    }

    override suspend fun insertHoliday(holiday: Holiday) {
        holidayDao.deleteHolidaysBySchoolId(holiday.schoolId?:"")
        holidayDao.insertHoliday(holiday)
    }

    override suspend fun deleteHolidaysBySchoolId(schoolId: String) {
        holidayDao.deleteHolidaysBySchoolId(schoolId)
    }
}