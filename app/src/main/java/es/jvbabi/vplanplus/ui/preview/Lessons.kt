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

    fun generateCanceledLesson(): Lesson {
        return Lesson(
            rooms = randomRoom().map { it.name },
            teacherIsChanged = Random.nextBoolean(),
            teachers = randomTeacher().map { it.acronym },
            start = DateUtils.getLocalDateTimeFromLocalDateAndTimeString("08:00", LocalDate.now()),
            end = DateUtils.getLocalDateTimeFromLocalDateAndTimeString("08:45", LocalDate.now()),
            info = "Stunde fällt aus",
            day = LocalDate.now(),
            lessonNumber = 1,
            roomIsChanged = Random.nextBoolean(),
            `class` = randomClass(),
            originalSubject = randomSubject(),
            changedSubject = "-",
            vpId = null
        )
    }

    fun generateLessons(count: Int = 4): List<Lesson> {
        val result = mutableListOf<Lesson>()
        repeat(count) { index ->
            result.add(
                Lesson(
                    rooms = randomRoom().map { it.name },
                    teacherIsChanged = Random.nextBoolean(),
                    teachers = randomTeacher().map { it.acronym },
                    start = DateUtils.getLocalDateTimeFromLocalDateAndTimeString("08:00", LocalDate.now()),
                    end = DateUtils.getLocalDateTimeFromLocalDateAndTimeString("08:45", LocalDate.now()),
                    info = "Info",
                    day = LocalDate.now(),
                    lessonNumber = index+1,
                    roomIsChanged = Random.nextBoolean(),
                    `class` = randomClass(),
                    originalSubject = randomSubject(),
                    changedSubject = if (Random.nextBoolean()) randomSubject() else null,
                    vpId = null
                )
            )
        }
        return result
    }

    private fun randomClass(): Classes {
        val school = School.generateRandomSchools(1).first()
        return listOf(
            Classes(
                name = "5a",
                school = school
            ),
            Classes(
                name = "5b",
                school = school
            ),
            Classes(
                name = "5c",
                school = school
            ),
            Classes(
                name = "6a",
                school = school
            ),
            Classes(
                name = "6b",
                school = school
            ),
            Classes(
                name = "6c",
                school = school
            ),
            Classes(
                name = "7a",
                school = school
            ),
            Classes(
                name = "7b",
                school = school
            ),
            Classes(
                name = "7c",
                school = school
            ),
            Classes(
                name = "8a",
                school = school
            ),
            Classes(
                name = "8b",
                school = school
            ),
            Classes(
                name = "8c",
                school = school
            ),

            Classes(
                name = "9a",
                school = school
            ),
            Classes(
                name = "9b",
                school = school
            ),
            Classes(
                name = "9c",
                school = school
            ),
        ).random()
    }

    private fun randomSubject(): String {
        return listOf("MA", "DEU", "ENG", "INF", "CH", "PH", "MU", "KU", "AST").random()
    }

    fun randomTeacher(): MutableList<Teacher> {
        val school = School.generateRandomSchools(1).first()
        return listOf(
            Teacher(
                school = school,
                acronym = "Mul"
            ),

            Teacher(
                school = school,
                acronym = "Sch"
            ),

            Teacher(
                school = school,
                acronym = "Sch"
            ),
            Teacher(
                school = school,
                acronym = "Fis"
            ),

            Teacher(
                school = school,
                acronym = "Mey"
            ),
            Teacher(
                school = school,
                acronym = "Web"
            ),
            Teacher(
                school = school,
                acronym = "Wag"
            ),
            Teacher(
                school = school,
                acronym = "Bec"
            ),
            Teacher(
                school = school,
                acronym = "Sch"
            ),
            Teacher(
                school = school,
                acronym = "Hof"
            ),
            Teacher(
                school = school,
                acronym = "Sch"
            ),
            Teacher(
                school = school,
                acronym = "Koc"
            ),
            Teacher(
                school = school,
                acronym = "Bau"
            ),
            Teacher(
                school = school,
                acronym = "Ric"
            ),
            Teacher(
                school = school,
                acronym = "Kle"
            )


        ).randomSubList(2, false)
    }

    fun randomRoom(): MutableList<Room> {
        val school = School.generateRandomSchools(1).first()
        return listOf(
            Room(
                school = school,
                name = "A1.01"
            ),
            Room(
                school = school,
                name = "A1.02"
            ),
            Room(
                school = school,
                name = "A1.03"
            ),
            Room(
                school = school,
                name = "101"
            ),
            Room(
                school = school,
                name = "102"
            ),
            Room(
                school = school,
                name = "103"
            ),
            Room(
                school = school,
                name = "104"
            ),
            Room(
                school = school,
                name = "201"
            ),
            Room(
                school = school,
                name = "202"
            ),
            Room(
                school = school,
                name = "203"
            ),
            Room(
                school = school,
                name = "204"
            ),
            Room(
                school = school,
                name = "K25"
            ),
            Room(
                school = school,
                name = "K26"
            ),
            Room(
                school = school,
                name = "TH 1"
            ),
            Room(
                school = school,
                name = "TH 2"
            ),
        ).randomSubList(2, false)
    }
}