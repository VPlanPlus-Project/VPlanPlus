package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import java.time.LocalDateTime
import java.util.UUID

class FakeLessonTimesRepository : LessonTimeRepository {

    override suspend fun insertLessonTime(lessonTime: LessonTime) {
        TODO("Not yet implemented")
    }

    override suspend fun insertLessonTimes(lessonTimes: List<LessonTime>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLessonTimes(`class`: Classes) {
        TODO("Not yet implemented")
    }

    override suspend fun getLessonTimesByClass(`class`: Classes): Map<Int, LessonTime> {
        TODO("Not yet implemented")
    }

    companion object {
        fun lessonTimesForClass(classId: UUID) = listOf(
            LessonTime(lessonNumber = 1, start = LocalDateTime.of(1970, 1, 1, 8, 0), end = LocalDateTime.of(1970, 1, 1, 8, 45), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 2, start = LocalDateTime.of(1970, 1, 1, 8, 50), end = LocalDateTime.of(1970, 1, 1, 9, 35), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 3, start = LocalDateTime.of(1970, 1, 1, 9, 55), end = LocalDateTime.of(1970, 1, 1, 10, 40), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 4, start = LocalDateTime.of(1970, 1, 1, 10, 45), end = LocalDateTime.of(1970, 1, 1, 11, 30), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 5, start = LocalDateTime.of(1970, 1, 1, 11, 35), end = LocalDateTime.of(1970, 1, 1, 12, 20), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 6, start = LocalDateTime.of(1970, 1, 1, 12, 25), end = LocalDateTime.of(1970, 1, 1, 13, 10), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 7, start = LocalDateTime.of(1970, 1, 1, 13, 15), end = LocalDateTime.of(1970, 1, 1, 14, 0), classLessonTimeRefId = classId),
            LessonTime(lessonNumber = 8, start = LocalDateTime.of(1970, 1, 1, 14, 5), end = LocalDateTime.of(1970, 1, 1, 14, 50), classLessonTimeRefId = classId)
        )
    }
}