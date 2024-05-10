package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

interface LessonRepository {
    fun getLessonsForTeacher(teacherId: UUID, date: LocalDate, version: Long): Flow<List<Lesson>?>
    fun getLessonsForClass(classId: UUID, date: LocalDate, version: Long): Flow<List<Lesson>?>
    fun getLessonsForRoom(roomId: UUID, date: LocalDate, version: Long): Flow<List<Lesson>?>
    fun getLessonsForSchoolByDate(schoolId: Int, date: LocalDate, version: Long): Flow<List<Lesson>>
    suspend fun getLessonsForProfile(profileId: UUID, date: LocalDate, version: Long): Flow<List<Lesson>?>

    suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate, version: Long)

    suspend fun insertLesson(dbLesson: DbLesson): Long

    suspend fun deleteAllLessons()

    suspend fun deleteLessonsByVersion(version: Long)

    suspend fun insertLessons(lessons: List<DbLesson>)
}