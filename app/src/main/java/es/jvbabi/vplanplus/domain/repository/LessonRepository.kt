package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface LessonRepository {
    fun getLessonsForTeacher(teacher: Teacher, date: LocalDate, version: Long): Flow<List<Lesson>?>
    fun getLessonsForGroup(group: Group, date: LocalDate, version: Long): Flow<List<Lesson>?>
    fun getLessonsForRoom(room: Room, date: LocalDate, version: Long): Flow<List<Lesson>?>
    fun getLessonsForSchoolByDate(school: School, date: LocalDate, version: Long): Flow<List<Lesson>>
    suspend fun getLessonsForProfile(profile: Profile, date: LocalDate, version: Long): Flow<List<Lesson>?>

    suspend fun deleteLessonForClass(`class`: Group, date: LocalDate, version: Long)

    suspend fun insertLesson(dbLesson: DbLesson): Long

    suspend fun deleteAllLessons()

    suspend fun deleteLessonsByVersion(version: Long)

    suspend fun insertLessons(lessons: List<DbLesson>)
}