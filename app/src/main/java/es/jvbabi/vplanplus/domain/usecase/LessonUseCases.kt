package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Deprecated("This class is not used anymore")
class LessonUseCases(
    private val lessonRepository: LessonRepository,
    private val planRepository: PlanRepository,
) {
    fun getLessonsForClass(classes: Classes, date: LocalDate, version: Long): Flow<Day> {
        return planRepository.getDayForClass(classes.classId, date, version)
    }

    fun getLessonsForTeacher(teacher: Teacher, date: LocalDate, version: Long): Flow<Day> {
        return planRepository.getDayForTeacher(teacher.teacherId, date, version)
    }

    fun getLessonsForRoom(room: Room, date: LocalDate, version: Long): Flow<Day> {
        return planRepository.getDayForRoom(room.roomId, date, version)
    }

    suspend fun deleteLessonsByVersion(version: Long) {
        lessonRepository.deleteLessonsByVersion(version)
    }
}