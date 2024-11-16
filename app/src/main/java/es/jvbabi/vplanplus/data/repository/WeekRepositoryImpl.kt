package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbWeek
import es.jvbabi.vplanplus.data.model.DbWeekType
import es.jvbabi.vplanplus.data.source.database.dao.WeekDao
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.model.WeekType
import es.jvbabi.vplanplus.domain.repository.WeekRepository
import es.jvbabi.vplanplus.util.MathTools.cantor
import java.time.LocalDate

class WeekRepositoryImpl(
    private val weekDao: WeekDao
) : WeekRepository {
    override suspend fun insertWeekType(school: School, name: String) {
        weekDao.upsertWeekType(DbWeekType(name, school.id))
    }

    override suspend fun getWeekTypesBySchool(school: School): List<WeekType> {
        return weekDao.getWeekTypesBySchool(school.id).map { it.toModel() }
    }

    override suspend fun insertWeek(school: School, weekType: WeekType, startDate: LocalDate, endDate: LocalDate, weekNumber: Int) {
        weekDao.upsertWeek(DbWeek(
            id = cantor(school.id, weekNumber),
            weekTypeId = weekType.id,
            schoolId = school.id,
            startDate = startDate,
            endDate = endDate,
            weekNumber = weekNumber
        ))
    }

    override suspend fun getWeeksBySchool(school: School): List<Week> {
        return weekDao.getWeeksBySchool(school.id).map { it.toModel() }
    }
}