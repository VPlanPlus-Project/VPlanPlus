package es.jvbabi.vplanplus.feature.main_grades.view.data.source.example

import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Teacher

object TeacherExample {

    val teachers = listOf(
        Teacher(
            id = 1,
            short = "MM",
            firstname = "Max",
            lastname = "Mustermann"
        ),
        Teacher(
            id = 2,
            short = "EM",
            firstname = "Erika",
            lastname = "Musterfrau"
        ),
        Teacher(
            id = 3,
            short = "HT",
            firstname = "Hans",
            lastname = "Test"
        )
    )
}