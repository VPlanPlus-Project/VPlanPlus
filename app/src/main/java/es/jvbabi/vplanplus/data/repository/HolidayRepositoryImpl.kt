package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.HolidayDao
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.DayType
import java.time.LocalDate

class HolidayRepositoryImpl(
    private val holidayDao: HolidayDao,
    private val schoolRepository: SchoolRepository
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
        holidays.map { it.schoolHolidayRefId }.toSet().forEach {
            holidayDao.deleteHolidaysBySchoolId(it?:return@forEach)
        }
        holidays.forEach {
            holidayDao.insertHoliday(it)
        }
    }

    override suspend fun insertHoliday(holiday: Holiday) {
        holidayDao.find(holiday.schoolHolidayRefId, holiday.date)?.let {
            holidayDao.deleteHoliday(it)
        }
        holidayDao.insertHoliday(holiday)
    }

    override suspend fun deleteHolidaysBySchoolId(schoolId: Long) {
        holidayDao.deleteHolidaysBySchoolId(schoolId)
    }

    override fun isHoliday(schoolId: Long, date: LocalDate): Boolean {
        return holidayDao.find(schoolId, date) != null
    }

    override fun getDayType(schoolId: Long, date: LocalDate): DayType {
        val school = schoolRepository.getSchoolFromId(schoolId)
        return if (isHoliday(schoolId, date)) DayType.HOLIDAY
        else if (date.dayOfWeek.value > school.daysPerWeek) DayType.WEEKEND
        else DayType.DATA
    }
}