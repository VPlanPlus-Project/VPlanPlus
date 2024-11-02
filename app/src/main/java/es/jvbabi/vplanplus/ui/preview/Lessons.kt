package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.randomSubList
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

object Lessons {

    fun generateLessons(count: Int = 4, isCurrentLesson: Boolean = false): List<Lesson> {
        val result = mutableListOf<Lesson>()
        val start = if (isCurrentLesson) "${LocalDateTime.now().hour}:15" else "08:00"
        val end = if (isCurrentLesson) "${LocalDateTime.now().hour + 1}:30" else "08:45"

        repeat(count) { index ->
            result.add(
                Lesson.SubstitutionPlanLesson(
                    rooms = randomRoom(),
                    teacherIsChanged = Random.nextBoolean(),
                    teachers = randomTeacher(),
                    start = DateUtils.zonedDateFromTimeStringAndDate(start, LocalDate.now()),
                    end = DateUtils.zonedDateFromTimeStringAndDate(end, LocalDate.now()),
                    info = "Info",
                    lessonNumber = index + 1,
                    roomIsChanged = Random.nextBoolean(),
                    group = randomClass(),
                    subject = randomSubject(),
                    changedSubject = if (Random.nextBoolean()) randomSubject() else null,
                    defaultLesson = null,
                    id = UUID.randomUUID(),
                    week = null,
                    weekType = null,
                    roomBooking = null
                )
            )
        }
        return result
    }

    private fun randomClass(): Group {
        val school = SchoolPreview.generateRandomSchools(1).first()
        return listOf(
            Group(
                name = "5a",
                groupId = 1,
                school = school,
                isClass = true
            ),
            Group(
                name = "5b",
                school = school,
                groupId = 2,
                isClass = true
            ),
            Group(
                name = "5c",
                school = school,
                groupId = 3,
                isClass = true
            ),
            Group(
                name = "6a",
                school = school,
                groupId = 4,
                isClass = true
            ),
            Group(
                name = "6b",
                school = school,
                groupId = 5,
                isClass = true
            ),
            Group(
                name = "6c",
                school = school,
                groupId = 6,
                isClass = true
            ),
            Group(
                name = "7a",
                school = school,
                groupId = 7,
                isClass = true
            ),
            Group(
                name = "7b",
                school = school,
                groupId = 8,
                isClass = true
            ),
            Group(
                name = "7c",
                school = school,
                groupId = 9,
                isClass = true
            ),
            Group(
                name = "8a",
                school = school,
                groupId = 10,
                isClass = true
            ),
            Group(
                name = "8b",
                school = school,
                groupId = 11,
                isClass = true
            ),
            Group(
                name = "8c",
                school = school,
                groupId = 12,
                isClass = true
            ),

            Group(
                name = "9a",
                school = school,
                groupId = 13,
                isClass = true
            ),
            Group(
                name = "9b",
                school = school,
                groupId = 14,
                isClass = true
            ),
            Group(
                name = "9c",
                school = school,
                groupId = 15,
                isClass = true
            ),
        ).random()
    }

    private fun randomSubject(): String {
        return listOf("MA", "DEU", "ENG", "INF", "CH", "PH", "MU", "KU", "AST").random()
    }

    private fun randomTeacher(): MutableList<Teacher> {
        val school = SchoolPreview.generateRandomSchools(1).first()
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

    private fun randomRoom(): MutableList<Room> {
        val school = SchoolPreview.generateRandomSchools(1).first()
        return listOf(
            Room(
                school = school,
                name = "A1.01",
                roomId = 1
            ),
            Room(
                school = school,
                name = "A1.02",
                roomId = 2
            ),
            Room(
                school = school,
                name = "A1.03",
                roomId = 3
            ),
            Room(
                school = school,
                name = "101",
                roomId = 4
            ),
            Room(
                school = school,
                name = "102",
                roomId = 5
            ),
            Room(
                school = school,
                name = "103",
                roomId = 6
            ),
            Room(
                school = school,
                name = "104",
                roomId = 7
            ),
            Room(
                school = school,
                name = "201",
                roomId = 8
            ),
            Room(
                school = school,
                name = "202",
                roomId = 9
            ),
            Room(
                school = school,
                name = "203",
                roomId = 10
            ),
            Room(
                school = school,
                name = "204",
                roomId = 11
            ),
            Room(
                school = school,
                name = "K25",
                roomId = 12
            ),
            Room(
                school = school,
                name = "K26",
                roomId = 13
            ),
            Room(
                school = school,
                name = "TH 1",
                roomId = 14
            ),
            Room(
                school = school,
                name = "TH 2",
                roomId = 15
            ),
        ).randomSubList(2, false)
    }
}