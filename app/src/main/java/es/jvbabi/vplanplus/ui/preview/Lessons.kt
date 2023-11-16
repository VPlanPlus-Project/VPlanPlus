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
                schoolClassRefId = 1
            ),
            Classes(
                className = "5b",
                schoolClassRefId = 1
            ),
            Classes(
                className = "5c",
                schoolClassRefId = 1
            ),
            Classes(
                className = "6a",
                schoolClassRefId = 1
            ),
            Classes(
                className = "6b",
                schoolClassRefId = 1
            ),
            Classes(
                className = "6c",
                schoolClassRefId = 1
            ),
            Classes(
                className = "7a",
                schoolClassRefId = 1
            ),
            Classes(
                className = "7b",
                schoolClassRefId = 1
            ),
            Classes(
                className = "7c",
                schoolClassRefId = 1
            ),
            Classes(
                className = "8a",
                schoolClassRefId = 1
            ),
            Classes(
                className = "8b",
                schoolClassRefId = 1
            ),
            Classes(
                className = "8c",
                schoolClassRefId = 1
            ),

            Classes(
                className = "9a",
                schoolClassRefId = 1
            ),
            Classes(
                className = "9b",
                schoolClassRefId = 1
            ),
            Classes(
                className = "9c",
                schoolClassRefId = 1
            ),
        ).random()
    }

    private fun randomSubject(): String {
        return listOf("MA", "DEU", "ENG", "INF", "CH", "PH", "MU", "KU", "AST").random()
    }

    fun randomTeacher(): MutableList<Teacher> {
        return listOf(
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Mul"
            ),

            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Sch"
            ),

            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Sch"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Fis"
            ),

            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Mey"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Web"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Wag"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Bec"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Sch"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Hof"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Sch"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Koc"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Bau"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Ric"
            ),
            Teacher(
                schoolTeacherRefId = 1,
                acronym = "Kle"
            )


        ).randomSubList(2, false)
    }

    fun randomRoom(): MutableList<Room> {
        return listOf(
            Room(
                schoolRoomRefId = 1,
                name = "A1.01"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "A1.02"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "A1.03"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "101"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "102"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "103"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "104"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "201"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "202"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "203"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "204"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "K25"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "K26"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "TH 1"
            ),
            Room(
                schoolRoomRefId = 1,
                name = "TH 2"
            ),
        ).randomSubList(2, false)
    }
}