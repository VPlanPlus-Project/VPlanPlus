package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.ui.screens.home.Day
import es.jvbabi.vplanplus.ui.screens.home.DayType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class LessonUseCases(
    private val lessonRepository: LessonRepository,
) {
    fun getLessonsForClass(classes: Classes, date: LocalDate): Flow<Day> {
        return lessonRepository.getLessonsForClass(classes.id!!, date)
            .map { dayPair ->
                if (dayPair.first != DayType.DATA) Day(dayType = dayPair.first)
                else {
                    Day(
                        dayType = DayType.DATA,
                        lessons = dayPair.second
                    )
                }
            }
    }

    fun getLessonsForTeacher(teacher: Teacher, date: LocalDate): Flow<Day> {
        return lessonRepository.getLessonsForTeacher(teacher.id!!, date)
            .map { dayPair ->
                if (dayPair.first != DayType.DATA) Day(dayType = dayPair.first)
                else {
                    Day(
                        dayType = DayType.DATA,
                        lessons = dayPair.second
                    )
                }
            }
    }

    fun getLessonsForRoom(room: Room, date: LocalDate): Flow<Day> {
        return lessonRepository.getLessonsForRoom(room.id!!, date)
            .map { dayPair ->
                if (dayPair.first != DayType.DATA) Day(dayType = dayPair.first)
                else {
                    Day(
                        dayType = DayType.DATA,
                        lessons = dayPair.second
                    )

                }
            }
    }
}