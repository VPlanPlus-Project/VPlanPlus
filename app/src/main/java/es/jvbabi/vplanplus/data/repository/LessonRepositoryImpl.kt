package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.LessonDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomProfile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.TeacherProfile
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@ExperimentalCoroutinesApi
class LessonRepositoryImpl(
    private val lessonDao: LessonDao
) : LessonRepository {

    private val converter = ZonedDateTimeConverter()

    override fun getLessonsForGroup(group: Group, date: LocalDate, version: Long): Flow<List<Lesson>?> {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))
        return lessonDao.getLessonsByGroup(timestamp = timestamp, version = version, group.groupId)
            .map { lessons ->
                (if (lessons.isEmpty()) null
                else lessons.map { lesson ->
                    lesson.toModel()
                })
            }
    }

    override fun getLessonsForTeacher(teacher: Teacher, date: LocalDate, version: Long): Flow<List<Lesson>?> {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))
        return lessonDao.getLessons(timestamp = timestamp, version = version)
            .map { lessons ->
                (if (lessons.isEmpty()) null
                else lessons.map { lesson ->
                    lesson.toModel()
                }).let { modeledLessons ->
                    modeledLessons?.filter { it.teachers.any { t -> t.teacherId == teacher.teacherId } }
                }
            }
    }

    override fun getLessonsForRoom(room: Room, date: LocalDate, version: Long): Flow<List<Lesson>?> {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))
        return lessonDao.getLessons(timestamp = timestamp, version = version)
            .map { lessons ->
                (if (lessons.isEmpty()) null
                else lessons.map { lesson ->
                    lesson.toModel()
                }).let { modeledLessons ->
                    modeledLessons?.filter { it.rooms.any { r -> r.roomId == room.roomId } }
                }
            }
    }

    override fun getLessonsForSchoolByDate(school: School, date: LocalDate, version: Long): Flow<List<Lesson>> {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))
        return lessonDao.getLessons(timestamp = timestamp, version = version).map { lessons ->
            lessons
                .map { it.toModel() }
                .filter { it.group.school.id == school.id || it.teachers.any { t -> t.school.id == school.id } || it.rooms.any { r -> r.school.id == school.id } }
        }
    }

    override fun getLessonsForProfile(
        profile: Profile,
        date: LocalDate,
        version: Long
    ): Flow<List<Lesson>?> {
        return when (profile) {
            is ClassProfile -> getLessonsForGroup(profile.group, date, version)
            is TeacherProfile -> getLessonsForTeacher(profile.teacher, date, version)
            is RoomProfile -> getLessonsForRoom(profile.room, date, version)
        }
    }

    override suspend fun deleteLessonForClass(`class`: Group, date: LocalDate, version: Long) {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))
        lessonDao.deleteLessonsByGroupAndDate(`class`.groupId, timestamp, version)
    }

    override suspend fun insertLesson(dbLesson: DbLesson): Long {
        return lessonDao.insertLesson(dbLesson)
    }

    override suspend fun deleteAllLessons() {
        lessonDao.deleteAll()
    }

    override suspend fun deleteLessonsByVersion(version: Long) {
        lessonDao.deleteLessonsByVersion(version)
    }

    override suspend fun insertLessons(lessons: List<DbLesson>) {
        lessonDao.insertLessons(lessons)
    }
}