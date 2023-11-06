package es.jvbabi.vplanplus.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.model.Profile
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
    suspend fun getTodayLessons(profile: Profile): List<Lesson> {
        Log.d("HomeUseCases", "getTodayLessons: ${profile.type}")
        val lessons = when (profile.type) {
            0 -> {
                lessonRepository.getLessonsForClass(profile.referenceId, LocalDate.now())
            }
            1 -> {
                lessonRepository.getLessonsForTeacher(profile.referenceId, LocalDate.now())
            }
            else -> null
        }!!
        return lessons.sortedBy { it.lesson }.map {
            try {
                val `class` = classRepository.getClassById(it.classId)
                val lessonTime = lessonTimeRepository.getLessonTimesByClass(`class`)[it.lesson]
                Lesson(
                    className = `class`.className,
                    lessonNumber = it.lesson,
                    info = it.info,
                    roomChanged = it.roomIsChanged,
                    room = if (it.roomId == null) "-" else roomRepository.getRoomById(it.roomId).name,
                    subjectChanged = it.changedSubject != null,
                    subject = it.changedSubject ?: it.originalSubject,
                    teacherChanged = it.changedTeacherId != null,
                    teacher = teacherRepository.getTeacherById(it.changedTeacherId?:it.originalTeacherId?:-1)?.acronym ?: "-",
                    start = lessonTime.start,
                    end = lessonTime.end

                )
            } catch (e: Exception) {
                Log.e("HomeUseCases", "getTodayLessons: ${e.stackTraceToString()}")
                Lesson(
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