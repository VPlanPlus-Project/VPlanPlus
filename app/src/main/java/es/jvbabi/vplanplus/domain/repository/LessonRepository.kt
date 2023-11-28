package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface LessonRepository {
    fun getLessonsForTeacher(teacherId: Long, date: LocalDate, version: Long): Flow<List<Lesson>?>
    fun getLessonsForClass(classId: Long, date: LocalDate, version: Long): Flow<List<Lesson>?>
    fun getLessonsForRoom(roomId: Long, date: LocalDate, version: Long): Flow<List<Lesson>?>

    suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate, version: Long)

    suspend fun insertLesson(dbLesson: DbLesson): Long

    suspend fun deleteAllLessons()

    suspend fun deleteLessonsByVersion(version: Long)

    suspend fun insertLessons(lessons: List<DbLesson>)
}