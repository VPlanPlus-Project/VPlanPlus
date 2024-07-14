package es.jvbabi.vplanplus.feature.main_grades.view.data.source.example

import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Year
import java.time.LocalDate

object ExampleYear {
    fun exampleYear(): Year {
        return Year(
            id = 1,
            name = "2021/2022",
            from = LocalDate.parse("2021-08-01"),
            to = LocalDate.parse("2022-07-31")
        )
    }
}