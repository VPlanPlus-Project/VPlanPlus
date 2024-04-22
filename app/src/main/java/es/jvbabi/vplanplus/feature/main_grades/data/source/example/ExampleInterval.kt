package es.jvbabi.vplanplus.feature.main_grades.data.source.example

import es.jvbabi.vplanplus.feature.main_grades.domain.model.Interval
import java.time.LocalDate

object ExampleInterval {
    fun interval1(isSek2: Boolean): Interval {
        return Interval(
            id = 1,
            name = "1. Hj",
            type = if (isSek2) "Sek II" else "Sek I",
            from = LocalDate.parse("2021-08-01"),
            to = LocalDate.parse("2021-12-31"),
            includedIntervalId = 2
        )
    }

    fun interval2(isSek2: Boolean): Interval {
        return Interval(
            id = 2,
            name = "2. Hj",
            type = if (isSek2) "Sek II" else "Sek I",
            from = LocalDate.parse("2022-01-01"),
            to = LocalDate.parse("2022-07-31"),
            includedIntervalId = 1
        )
    }
}