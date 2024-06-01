package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.LessonDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@ExperimentalCoroutinesApi
class LessonRepositoryImpl(
    private val lessonDao: LessonDao,
    private val profileRepository: ProfileRepository
) : LessonRepository {

    private val converter = ZonedDateTimeConverter()

    override fun getLessonsForClass(classId: UUID, date: LocalDate, version: Long): Flow<List<Lesson>?> {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))
        return lessonDao.getLessonsByClass(classId, timestamp, version)
            .map { lessons ->
                if (lessons.isEmpty()) null
                else lessons.map { lesson ->
                    lesson.toModel()
                }
            }
    }

    override fun getLessonsForTeacher(teacherId: UUID, date: LocalDate, version: Long): Flow<List<Lesson>?> {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))
        return lessonDao.getLessonsByTeacher(teacherId, timestamp, version)
            .map { lessons ->
                if (lessons.isEmpty()) null
                else lessons.map { lesson ->
                    lesson.toModel()
                }
            }
    }

    override fun getLessonsForRoom(roomId: UUID, date: LocalDate, version: Long): Flow<List<Lesson>?> {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))
        return lessonDao.getLessonsByRoom(roomId, timestamp, version)
            .map { lessons ->
                if (lessons.isEmpty()) null
                else lessons.map { lesson ->
                    lesson.toModel()
                }
            }
    }

    override fun getLessonsForSchoolByDate(schoolId: Int, date: LocalDate, version: Long): Flow<List<Lesson>> {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))

        return lessonDao.getLessonsForSchool(schoolId, timestamp, version).map { lessons ->
            lessons.map {
                it.toModel()
            }
        }
    }

    override suspend fun getLessonsForProfile(
        profileId: UUID,
        date: LocalDate,
        version: Long
    ): Flow<List<Lesson>?> {
        val profile = profileRepository.getProfileById(profileId).first() ?: return emptyFlow()

        return when (profile.type) {
            ProfileType.STUDENT -> getLessonsForClass(profile.referenceId, date, version)
            ProfileType.TEACHER -> getLessonsForTeacher(profile.referenceId, date, version)
            ProfileType.ROOM -> getLessonsForRoom(profile.referenceId, date, version)
        }
    }

    override suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate, version: Long) {
        val timestamp = converter.zonedDateTimeToTimestamp(ZonedDateTime.of(date, LocalTime.MIN, ZoneId.of("UTC")))
        lessonDao.deleteLessonsByClassAndDate(`class`.classId, timestamp, version)
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