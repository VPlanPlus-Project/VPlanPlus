package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
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
            LessonTime(lessonNumber = 1, classLessonTimeRefId = classId, from = 7*60*60+30*60, to = 8*60*60+15*60), // 7:30 - 8:15
            LessonTime(lessonNumber = 2, classLessonTimeRefId = classId, from = 8*60*60+25*60, to = 9*60*60+10*60), // 8:25 - 9:10
            LessonTime(lessonNumber = 3, classLessonTimeRefId = classId, from = 9*60*60+30*60, to = 10*60*60+15*60), // 9:30 - 10:15
            LessonTime(lessonNumber = 4, classLessonTimeRefId = classId, from = 10*60*60+25*60, to = 11*60*60+10*60), // 10:25 - 11:10
            LessonTime(lessonNumber = 5, classLessonTimeRefId = classId, from = 11*60*60+20*60, to = 12*60*60+5*60), // 11:20 - 12:05
            LessonTime(lessonNumber = 6, classLessonTimeRefId = classId, from = 12*60*60+15*60, to = 13*60*60), // 12:15 - 13:00
            LessonTime(lessonNumber = 7, classLessonTimeRefId = classId, from = 13*60*60+30*60, to = 14*60*60+15*60), // 13:30 - 14:15
            LessonTime(lessonNumber = 8, classLessonTimeRefId = classId, from = 14*60*60+25*60, to = 15*60*60+10*60), // 14:25 - 15:10
        )
    }
}