package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.randomSubList
import java.time.LocalDate
import kotlin.random.Random

object Lessons {
    fun generateLessons(count: Int = 4): List<Lesson> {
        val result = mutableListOf<Lesson>()
        repeat(count) { index ->
            result.add(
                Lesson(
                    rooms = randomRoom(),
                    teacherIsChanged = Random.nextBoolean(),
                    teachers = randomTeacher(),
                    start = DateUtils.getLocalDateTimeFromLocalDateAndTimeString("08:00", LocalDate.now()),
                    end = DateUtils.getLocalDateTimeFromLocalDateAndTimeString("08:45", LocalDate.now()),
                    info = "Info",
                    day = LocalDate.now(),
                    lessonNumber = index,
                    roomIsChanged = Random.nextBoolean(),
                    `class` = randomClass(),
                    originalSubject = randomSubject(),
                    changedSubject = if (Random.nextBoolean()) randomSubject() else null
                )
            )
        }
        return result
    }

    private fun randomClass(): Classes {
        return listOf(
            Classes(
                className = "5a",
                schoolId = 1
            ),
            Classes(
                className = "5b",
                schoolId = 1
            ),
            Classes(
                className = "5c",
                schoolId = 1
            ),
            Classes(
                className = "6a",
                schoolId = 1
            ),
            Classes(
                className = "6b",
                schoolId = 1
            ),
            Classes(
                className = "6c",
                schoolId = 1
            ),
            Classes(
                className = "7a",
                schoolId = 1
            ),
            Classes(
                className = "7b",
                schoolId = 1
            ),
            Classes(
                className = "7c",
                schoolId = 1
            ),
            Classes(
                className = "8a",
                schoolId = 1
            ),
            Classes(
                className = "8b",
                schoolId = 1
            ),
            Classes(
                className = "8c",
                schoolId = 1
            ),

            Classes(
                className = "9a",
                schoolId = 1
            ),
            Classes(
                className = "9b",
                schoolId = 1
            ),
            Classes(
                className = "9c",
                schoolId = 1
            ),
        ).random()
    }

    private fun randomSubject(): String {
        return listOf("MA", "DEU", "ENG", "INF", "CH", "PH", "MU", "KU", "AST").random()
    }

    fun randomTeacher(): MutableList<Teacher> {
        return listOf(
            Teacher(
                schoolId = 1,
                acronym = "Mul"
            ),

            Teacher(
                schoolId = 1,
                acronym = "Sch"
            ),

            Teacher(
                schoolId = 1,
                acronym = "Sch"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Fis"
            ),

            Teacher(
                schoolId = 1,
                acronym = "Mey"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Web"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Wag"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Bec"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Sch"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Hof"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Sch"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Koc"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Bau"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Ric"
            ),
            Teacher(
                schoolId = 1,
                acronym = "Kle"
            )


        ).randomSubList(2, false)
    }

    fun randomRoom(): MutableList<Room> {
        return listOf(
            Room(
                schoolId = 1,
                name = "A1.01"
            ),
            Room(
                schoolId = 1,
                name = "A1.02"
            ),
            Room(
                schoolId = 1,
                name = "A1.03"
            ),
            Room(
                schoolId = 1,
                name = "101"
            ),
            Room(
                schoolId = 1,
                name = "102"
            ),
            Room(
                schoolId = 1,
                name = "103"
            ),
            Room(
                schoolId = 1,
                name = "104"
            ),
            Room(
                schoolId = 1,
                name = "201"
            ),
            Room(
                schoolId = 1,
                name = "202"
            ),
            Room(
                schoolId = 1,
                name = "203"
            ),
            Room(
                schoolId = 1,
                name = "204"
            ),
            Room(
                schoolId = 1,
                name = "K25"
            ),
            Room(
                schoolId = 1,
                name = "K26"
            ),
            Room(
                schoolId = 1,
                name = "TH 1"
            ),
            Room(
                schoolId = 1,
                name = "TH 2"
            ),
        ).randomSubList(2, false)
    }
}