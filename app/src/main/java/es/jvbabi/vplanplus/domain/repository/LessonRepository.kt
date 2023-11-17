package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.screens.home.DayType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface LessonRepository {
    suspend fun getLessonsForClass(classId: Long, date: LocalDate): Flow<Pair<DayType, List<Lesson>>>
    suspend fun getLessonsForTeacher(teacherId: Long, date: LocalDate): Flow<Pair<DayType, List<Lesson>>>
    suspend fun getLessonsForRoom(roomId: Long, date: LocalDate): Flow<Pair<DayType, List<Lesson>>>

    suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate)

    suspend fun insertLesson(dbLesson: DbLesson): Long

    suspend fun deleteAllLessons()
}