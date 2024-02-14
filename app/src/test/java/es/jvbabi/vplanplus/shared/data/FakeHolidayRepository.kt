package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import java.time.LocalDate

class FakeHolidayRepository(
    private val fakeSchoolRepository: FakeSchoolRepository
) : HolidayRepository {
    private val holidays = mutableListOf<Holiday>()

    override suspend fun getHolidaysBySchoolId(schoolId: Long): List<Holiday> {
        return holidays.filter { it.schoolHolidayRefId == schoolId }
    }

    override suspend fun getTodayHoliday(schoolId: Long): Holiday? {
        return holidays.firstOrNull { it.schoolHolidayRefId == schoolId && it.date == LocalDate.now() }
    }

    override suspend fun insertHoliday(holiday: Holiday) {
        holidays.add(holiday)
    }

    override suspend fun replaceHolidays(holidays: List<Holiday>) {
        this.holidays.clear()
        this.holidays.addAll(holidays)
    }

    override suspend fun deleteHolidaysBySchoolId(schoolId: Long) {
        holidays.removeIf { it.schoolHolidayRefId == schoolId }
    }

    override suspend fun isHoliday(schoolId: Long, date: LocalDate): Boolean {
        return holidays.any { it.schoolHolidayRefId == schoolId && it.date == date }
    }

    override suspend fun getDayType(schoolId: Long, date: LocalDate): DayType {
        val daysPerWeek = fakeSchoolRepository.getSchoolFromId(schoolId)!!.daysPerWeek
        if (date.dayOfWeek.value > daysPerWeek) {
            return DayType.WEEKEND
        }
        return if (isHoliday(schoolId, date)) DayType.HOLIDAY else DayType.NORMAL
    }

    companion object {
        val dates = listOf(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 4, 19),
            LocalDate.of(2024, 4, 22),
            LocalDate.of(2024, 5, 1),
            LocalDate.of(2024, 5, 9),
            LocalDate.of(2024, 5, 20),
            LocalDate.of(2024, 10, 3),
            LocalDate.of(2024, 12, 25),
            LocalDate.of(2024, 12, 26)
        )

        fun holidaysForSchool(schoolId: Long) = dates.map { Holiday(date = it, schoolHolidayRefId = schoolId)}
    }
}