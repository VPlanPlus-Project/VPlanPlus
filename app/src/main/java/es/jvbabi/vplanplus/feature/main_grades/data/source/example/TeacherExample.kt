package es.jvbabi.vplanplus.feature.main_grades.data.source.example

import es.jvbabi.vplanplus.feature.main_grades.domain.model.Teacher

object TeacherExample {

    val teachers = listOf(
        Teacher(
            id = 1,
            firstname = "Max",
            lastname = "Mustermann"
        ),
        Teacher(
            id = 2,
            firstname = "Erika",
            lastname = "Musterfrau"
        ),
        Teacher(
            id = 3,
            firstname = "Hans",
            lastname = "Test"
        )
    )
}