package es.jvbabi.vplanplus.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.ui.screens.home.Lesson
import java.time.LocalDate

class HomeUseCases(
    private val lessonRepository: LessonRepository,
    private val classRepository: ClassRepository,
    private val lessonTimeRepository: LessonTimeRepository
) {
    suspend fun getLessons(profile: Profile, date: LocalDate): List<Lesson> {
        val lessons = when (profile.type) {
            ProfileType.STUDENT -> {
                lessonRepository.getLessonsForClass(profile.referenceId, date)
            }
            ProfileType.TEACHER -> {
                lessonRepository.getLessonsForTeacher(profile.referenceId, date)
            }
            ProfileType.ROOM -> {
                lessonRepository.getLessonsForRoom(profile.referenceId, date)
            }
        }
        var id = -1L
        return lessons.sortedBy { it.lesson }.map {
            id++
            try {
                val `class` = classRepository.getClassById(it.classId)
                val lessonTime = lessonTimeRepository.getLessonTimesByClass(`class`).getOrNull(it.lesson)
                Lesson(
                    id = id,
                    className = `class`.className,
                    lessonNumber = it.lesson,
                    info = it.info,
                    roomChanged = it.roomIsChanged,
                    room = if (it.rooms.isNotEmpty()) it.rooms.map { room -> room.name } else listOf("-"),
                    subjectChanged = it.changedSubject != null,
                    subject = it.changedSubject ?: it.originalSubject,
                    teacherChanged = it.teacherIsChanged,
                    teacher = if (it.teachers.isNotEmpty()) it.teachers.map { teacher -> teacher.acronym } else listOf("-"),
                    start = lessonTime?.start?:"",
                    end = lessonTime?.end?:""
                )
            } catch (e: Exception) {
                Log.e("HomeUseCases", "getTodayLessons: ${e.stackTraceToString()}")
                Lesson(
                    id = id,
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
    }
}