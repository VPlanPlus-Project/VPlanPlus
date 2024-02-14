package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.repository.WeekRepository

class FakeWeekRepository : WeekRepository {
    private val weeks = mutableListOf<Week>()

    override suspend fun replaceWeeks(weeks: List<Week>) {
        this.weeks.clear()
        this.weeks.addAll(weeks)
    }
}