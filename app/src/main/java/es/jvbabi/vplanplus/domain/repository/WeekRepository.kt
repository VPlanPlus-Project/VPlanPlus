package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Week

interface WeekRepository {
    suspend fun insertWeeks(weeks: List<Week>)
}