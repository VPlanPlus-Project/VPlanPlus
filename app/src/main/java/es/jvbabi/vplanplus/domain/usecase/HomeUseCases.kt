package es.jvbabi.vplanplus.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.ui.screens.home.Day
import es.jvbabi.vplanplus.ui.screens.home.DayType
import es.jvbabi.vplanplus.ui.screens.home.Lesson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HomeUseCases(
    private val lessonRepository: LessonRepository,
    private val classRepository: ClassRepository,
    private val lessonTimeRepository: LessonTimeRepository
) {
    fun getLessons(profile: Profile, date: LocalDate): Flow<Day> {
        return when (profile.type) {
            ProfileType.STUDENT -> {
                lessonRepository.getLessonsForClass(profile.referenceId, date)
            }
            ProfileType.TEACHER -> {
                lessonRepository.getLessonsForTeacher(profile.referenceId, date)
            }
            ProfileType.ROOM -> {
                lessonRepository.getLessonsForRoom(profile.referenceId, date)
            }
        }.map { dayPair ->
            if (dayPair.first != DayType.DATA) Day(dayType = dayPair.first)
            else {
                Day(
                    dayType = DayType.DATA,
                    lessons = dayPair.second.sortedBy { it.lesson }.map { lesson ->
                        try {
                            val `class` = classRepository.getClassById(lesson.classId)
                            val lessonTime = lessonTimeRepository.getLessonTimesByClass(`class`).firstOrNull { it.lessonNumber == lesson.lesson }
                            Lesson(
                                id = lesson.id!!,
                                className = `class`.className,
                                lessonNumber = lesson.lesson,
                                info = lesson.info,
                                roomChanged = lesson.roomIsChanged,
                                room = if (lesson.rooms.isNotEmpty()) lesson.rooms.map { room -> room.name } else listOf("-"),
                                subjectChanged = lesson.changedSubject != null,
                                subject = lesson.changedSubject ?: lesson.originalSubject,
                                teacherChanged = lesson.teacherIsChanged,
                                teacher = if (lesson.teachers.isNotEmpty()) lesson.teachers.map { teacher -> teacher.acronym } else listOf("-"),
                                start = lessonTime?.start ?: "",
                                end = lessonTime?.end ?: ""
                            )
                        } catch (e: Exception) {
                            Log.e("HomeUseCases", "getTodayLessons: ${e.stackTraceToString()}")
                            Lesson(
                                id = lesson.id!!,
                                className = "Error",
                                subject = e.message ?: "Error",
                                teacher = listOf("Error"),
                                room = listOf("Error"),
                                subjectChanged = false,
                                teacherChanged = false,
                                roomChanged = false,
                                start = "00:00",
                                end = "23:59",
                                lessonNumber = 0,
                                info = e.stackTraceToString()
                            )
                        }
                    }
                )
            }
        }
    }
}