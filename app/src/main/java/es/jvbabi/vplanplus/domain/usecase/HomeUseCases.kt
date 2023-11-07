package es.jvbabi.vplanplus.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.ui.screens.home.Lesson
import java.time.LocalDate

class HomeUseCases(
    private val lessonRepository: LessonRepository,
    private val teacherRepository: TeacherRepository,
    private val classRepository: ClassRepository,
    private val roomRepository: RoomRepository,
    private val lessonTimeRepository: LessonTimeRepository
) {
    suspend fun getLessons(profile: Profile, date: LocalDate): List<Lesson> {
        Log.d("HomeUseCases", "getLessons: ${profile.type} at $date")
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
                    room = if (it.roomId == null) "-" else roomRepository.getRoomById(it.roomId).name,
                    subjectChanged = it.changedSubject != null,
                    subject = it.changedSubject ?: it.originalSubject,
                    teacherChanged = it.changedTeacherId != null,
                    teacher = teacherRepository.getTeacherById(it.changedTeacherId?:it.originalTeacherId?:-1)?.acronym ?: "-",
                    start = lessonTime?.start?:"",
                    end = lessonTime?.end?:""
                )
            } catch (e: Exception) {
                Log.e("HomeUseCases", "getTodayLessons: ${e.stackTraceToString()}")
                Lesson(
                    id = id,
                    className = "Error",
                    subject = e.message ?: "Error",
                    teacher = "Error",
                    room = "Error",
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