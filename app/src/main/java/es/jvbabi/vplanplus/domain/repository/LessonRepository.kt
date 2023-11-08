package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import java.time.LocalDate

interface LessonRepository {
    suspend fun getLessonsForClass(classId: Long, date: LocalDate): List<Lesson>
    suspend fun getLessonsForTeacher(teacherId: Long, date: LocalDate): List<Lesson>
    suspend fun getLessonsForRoom(roomId: Long, date: LocalDate): List<Lesson>

    suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate)

    suspend fun insertLesson(lesson: Lesson)

    suspend fun deleteAllLessons()
}