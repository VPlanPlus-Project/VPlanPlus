package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.HolidayDao
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.util.DateUtils

class HolidayRepositoryImpl(
    private val holidayDao: HolidayDao
) : HolidayRepository {
    override suspend fun getHolidaysBySchoolId(schoolId: Long): List<Holiday> {
        return holidayDao.getHolidaysBySchoolId(schoolId)
    }

    override suspend fun getTodayHoliday(schoolId: Long): Holiday? {
        return holidayDao.getHolidaysBySchoolId(schoolId).find {
            it.timestamp == DateUtils.getCurrentDayTimestamp()
        }
    }

    override suspend fun insertHolidays(holidays: List<Holiday>) {
        holidays.map { it.schoolId }.toSet().forEach {
            holidayDao.deleteHolidaysBySchoolId(it?:return@forEach)
        }
        holidays.forEach {
            holidayDao.insertHoliday(it)
        }
    }

    override suspend fun insertHoliday(holiday: Holiday) {
        holidayDao.find(holiday.schoolId, holiday.timestamp)?.let {
            holidayDao.deleteHoliday(it)
        }
        holidayDao.insertHoliday(holiday)
    }

    override suspend fun deleteHolidaysBySchoolId(schoolId: Long) {
        holidayDao.deleteHolidaysBySchoolId(schoolId)
    }
}