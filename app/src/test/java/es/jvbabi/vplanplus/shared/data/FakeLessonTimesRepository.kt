package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

class FakeLessonTimesRepository : LessonTimeRepository {

    private val lessonTimes = mutableListOf<LessonTime>()

    override suspend fun insertLessonTime(lessonTime: LessonTime) {
        lessonTimes.add(lessonTime)
    }

    override suspend fun insertLessonTimes(lessonTimes: List<LessonTime>) {
        this.lessonTimes.addAll(lessonTimes)
    }

    override suspend fun deleteLessonTimes(`class`: Classes) {
        lessonTimes.removeIf { it.classLessonTimeRefId == `class`.classId }
    }

    override suspend fun getLessonTimesByClass(`class`: Classes): Map<Int, LessonTime> {
        return lessonTimes.filter { it.classLessonTimeRefId == `class`.classId }
            .associateBy { it.lessonNumber }
    }

    companion object {
        fun lessonTimesForClass(classId: UUID) = listOf(
            LessonTime(lessonNumber = 1, start = ZonedDateTime.of(1970, 1, 1, 8, 0, 0, 0, ZoneId.of("UTC")), end = ZonedDateTime.of(1970, 1, 1, 8, 45, 0, 0, ZoneId.of("UTC")), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 2, start = ZonedDateTime.of(1970, 1, 1, 8, 50, 0, 0, ZoneId.of("UTC")), end = ZonedDateTime.of(1970, 1, 1, 9, 35, 0, 0, ZoneId.of("UTC")), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 3, start = ZonedDateTime.of(1970, 1, 1, 9, 55, 0, 0, ZoneId.of("UTC")), end = ZonedDateTime.of(1970, 1, 1, 10, 40, 0, 0, ZoneId.of("UTC")), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 4, start = ZonedDateTime.of(1970, 1, 1, 10, 45, 0, 0, ZoneId.of("UTC")), end = ZonedDateTime.of(1970, 1, 1, 11, 30, 0, 0, ZoneId.of("UTC")), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 5, start = ZonedDateTime.of(1970, 1, 1, 11, 35, 0, 0, ZoneId.of("UTC")), end = ZonedDateTime.of(1970, 1, 1, 12, 20, 0, 0, ZoneId.of("UTC")), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 6, start = ZonedDateTime.of(1970, 1, 1, 12, 25, 0, 0, ZoneId.of("UTC")), end = ZonedDateTime.of(1970, 1, 1, 13, 10, 0, 0, ZoneId.of("UTC")), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 7, start = ZonedDateTime.of(1970, 1, 1, 13, 15, 0, 0, ZoneId.of("UTC")), end = ZonedDateTime.of(1970, 1, 1, 14, 0, 0, 0, ZoneId.of("UTC")), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 8, start = ZonedDateTime.of(1970, 1, 1, 14, 5, 0, 0, ZoneId.of("UTC")), end = ZonedDateTime.of(1970, 1, 1, 14, 50, 0, 0, ZoneId.of("UTC")), classLessonTimeRefId = classId)
        )
    }
}