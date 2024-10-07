package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.HolidayDao
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import java.time.LocalDate

class HolidayRepositoryImpl(
    private val holidayDao: HolidayDao,
    private val schoolRepository: SchoolRepository
) : HolidayRepository {
    override suspend fun getHolidaysBySchoolId(schoolId: Int): List<Holiday> {
        return holidayDao.getHolidaysBySchoolId(schoolId)
    }

    override suspend fun getTodayHoliday(schoolId: Int): Holiday? {
        return holidayDao.getHolidaysBySchoolId(schoolId).find {
            it.date.isEqual(LocalDate.now())
        }
    }

    override suspend fun insertHoliday(schoolId: Int?, date: LocalDate) {
        holidayDao.insertHoliday(schoolId = schoolId, date = date)
    }

    override suspend fun deleteHolidaysBySchoolId(schoolId: Int) {
        holidayDao.deleteHolidaysBySchoolId(schoolId)
    }

    override suspend fun isHoliday(schoolId: Int, date: LocalDate): Boolean {
        return holidayDao.find(schoolId, date) != null
    }

    override suspend fun getDayType(schoolId: Int, date: LocalDate): DayType {
        val school = schoolRepository.getSchoolFromId(schoolId) ?: return DayType.NORMAL
        return if (isHoliday(schoolId, date)) DayType.HOLIDAY
        else if (date.dayOfWeek.value > school.daysPerWeek) DayType.WEEKEND
        else DayType.NORMAL
    }

    override suspend fun deleteAll() {
        holidayDao.deleteAll()
    }
}