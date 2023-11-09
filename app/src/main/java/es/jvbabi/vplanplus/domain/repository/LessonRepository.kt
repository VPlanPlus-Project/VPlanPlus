package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface LessonRepository {
    fun getLessonsForClass(classId: Long, date: LocalDate): Flow<List<Lesson>>
    fun getLessonsForTeacher(teacherId: Long, date: LocalDate): Flow<List<Lesson>>
    fun getLessonsForRoom(roomId: Long, date: LocalDate): Flow<List<Lesson>>

    suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate)

    suspend fun insertLesson(lesson: Lesson)

    suspend fun deleteAllLessons()
}