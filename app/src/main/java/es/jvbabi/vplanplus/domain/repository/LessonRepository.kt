package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Lesson
import java.time.LocalDate

interface LessonRepository {
    suspend fun getLessonsForClass(classId: Int, date: LocalDate): List<Pair<Lesson, DefaultLesson?>>
    suspend fun getLessonsForTeacher(teacherId: Int, date: LocalDate): List<Pair<Lesson, DefaultLesson?>>
    suspend fun getLessonsForRoom(roomId: Int, date: LocalDate): List<Pair<Lesson, DefaultLesson?>>

    suspend fun deleteLessonForClass(classId: Int, date: LocalDate)

    suspend fun insertLesson(lesson: Lesson)
}