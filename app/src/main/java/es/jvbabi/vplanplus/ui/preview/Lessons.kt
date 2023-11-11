package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.ui.screens.home.Lesson
import es.jvbabi.vplanplus.util.randomSubList
import kotlin.random.Random

object Lessons {
    fun generateModelLessons(count: Int = 4) :List<es.jvbabi.vplanplus.domain.model.Lesson> {
        val result = mutableListOf<es.jvbabi.vplanplus.domain.model.Lesson>()
        repeat(count) { index ->
            result.add(
                es.jvbabi.vplanplus.domain.model.Lesson(
                    id = index.toLong(),
                    lesson = index,
                    info = "Info",
                    originalSubject = randomSubject(),
                    changedSubject = if (Random.nextBoolean()) randomSubject() else null,
                    classId = 0,
                    dayTimestamp = 0,
                    roomIsChanged = Random.nextBoolean(),
                    teacherIsChanged = Random.nextBoolean(),
                    )
            )
        }
        return result
    }

    fun generateLessons(count: Int = 4): List<Lesson> {
        val result = mutableListOf<Lesson>()
        repeat(count) { index ->
            result.add(
                Lesson(
                    id = index.toLong(),
                    subject = randomSubject(),
                    teacher = randomTeacher(),
                    room = randomRoom(),
                    start = "08:00",
                    end = "08:45",
                    lessonNumber = index,
                    info = "Info",
                    subjectChanged = false,
                    teacherChanged = false,
                    roomChanged = false,
                    className = "10a",
                )
            )
        }
        return result
    }

    fun randomSubject(): String {
        return listOf("MA", "DEU", "ENG", "INF", "CH", "PH", "MU", "KU", "AST").random()
    }

    fun randomTeacher(): MutableList<String> {
        return listOf("FeN", "Era", "Mul", "Hfn", "ScA", "ScB", "Vol", "Bab", "Cre").randomSubList(2, false)
    }

    fun randomRoom(): MutableList<String> {
        return listOf("208", "209", "210", "211", "212", "213", "214", "215", "216", "TH 1", "TH 2").randomSubList(2, false)
    }
}