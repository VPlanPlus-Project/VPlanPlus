package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.HolidayDao
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import java.time.LocalDate

class HolidayRepositoryImpl(
    private val holidayDao: HolidayDao
) : HolidayRepository {
    override suspend fun getHolidaysBySchoolId(schoolId: Long): List<Holiday> {
        return holidayDao.getHolidaysBySchoolId(schoolId)
    }

    override suspend fun getTodayHoliday(schoolId: Long): Holiday? {
        return holidayDao.getHolidaysBySchoolId(schoolId).find {
            it.date.isEqual(LocalDate.now())
        }
    }

    /**
     * Deletes all holidays with the same schoolId as the ones in the list and inserts the new ones
     * @param holidays List of holidays to insert
     */
    override suspend fun replaceHolidays(holidays: List<Holiday>) {
        holidays.map { it.schoolId }.toSet().forEach {
            holidayDao.deleteHolidaysBySchoolId(it?:return@forEach)
        }
        holidays.forEach {
            holidayDao.insertHoliday(it)
        }
    }

    override suspend fun insertHoliday(holiday: Holiday) {
        holidayDao.find(holiday.schoolId, holiday.date)?.let {
            holidayDao.deleteHoliday(it)
        }
        holidayDao.insertHoliday(holiday)
    }

    override suspend fun deleteHolidaysBySchoolId(schoolId: Long) {
        holidayDao.deleteHolidaysBySchoolId(schoolId)
    }
}