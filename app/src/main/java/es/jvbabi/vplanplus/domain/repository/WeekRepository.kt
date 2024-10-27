package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.model.WeekType
import java.time.LocalDate

interface WeekRepository {
    suspend fun insertWeekType(school: School, name: String)
    suspend fun getWeekTypesBySchool(school: School): List<WeekType>

    suspend fun insertWeek(school: School, weekType: WeekType, startDate: LocalDate, endDate: LocalDate, weekNumber: Int)
    suspend fun getWeeksBySchool(school: School): List<Week>
}