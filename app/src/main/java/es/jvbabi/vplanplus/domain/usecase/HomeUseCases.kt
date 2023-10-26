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
            else -> null
        }!!
        return lessons.sortedBy { it.first.lesson }.map {
            try {
                val teacher = teacherRepository.getTeacherById(if (it.first.changedTeacherId != null) it.first.changedTeacherId!! else it.second!!.teacherId)
                val dbClass = classRepository.getClassById(it.first.classId)
                val room = roomRepository.getRoomById(it.first.roomId!!)
                val times = lessonTimeRepository.getLessonTimesByClassId(dbClass.id!!)
                var subject = if (it.first.changedSubject != null) it.first.changedSubject!! else it.second!!.subject
                if (subject == "---") subject = "-"
                Lesson(
                    className = dbClass.className,
                    subject = subject,
                    teacher = teacher?.acronym?:"-",
                    room = if(room.name == "&nbsp;") "-" else room.name,
                    subjectChanged = it.first.changedSubject != null,
                    teacherChanged = it.first.changedTeacherId != null,
                    roomChanged = it.first.roomIsChanged,
                    start = times.find { time -> time.lessonNumber == it.first.lesson }!!.start,
                    end = times.find { time -> time.lessonNumber == it.first.lesson }!!.end,
                    lessonNumber = it.first.lesson,
                    info = it.first.changedInfo
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