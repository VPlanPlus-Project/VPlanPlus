package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.WeekDao
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.repository.WeekRepository

class WeekRepositoryImpl(
    private val weekDao: WeekDao
) : WeekRepository {
    override suspend fun insertWeeks(weeks: List<Week>) {
        weeks.map { it.schoolId }.toSet().forEach {
            weekDao.deleteWeeksBySchoolId(it)
        }
        weeks.forEach {
            weekDao.insertWeek(it)
        }
    }
}