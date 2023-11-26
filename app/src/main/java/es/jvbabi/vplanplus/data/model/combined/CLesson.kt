package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbClass
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.DbTeacher
import es.jvbabi.vplanplus.data.source.database.crossover.LessonRoomCrossover
import es.jvbabi.vplanplus.data.source.database.crossover.LessonTeacherCrossover
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.util.DateUtils

data class CLesson(
    @Embedded val lesson: DbLesson,
    @Relation(
        parentColumn = "classLessonRefId", entityColumn = "classId", entity = DbClass::class
    ) val `class`: CClass,
    @Relation(
        parentColumn = "defaultLessonId",
        entityColumn = "defaultLessonId",
        entity = DbDefaultLesson::class
    ) val defaultLesson: CDefaultLesson?,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "teacherId",
        associateBy = Junction(
            value = LessonTeacherCrossover::class,
            parentColumn = "ltcLessonId",
            entityColumn = "ltcTeacherId"
        ),
        entity = DbTeacher::class
    )
    val teachers: List<DbTeacher>,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "roomId",
        associateBy = Junction(
            value = LessonRoomCrossover::class,
            parentColumn = "lrcLessonId",
            entityColumn = "lrcRoomId"
        ),
        entity = DbRoom::class
    )
    val rooms: List<DbRoom>,
    @Relation(
        parentColumn = "classLessonRefId",
        entityColumn = "classLessonTimeRefId",
        entity = LessonTime::class
    ) val lessonTimes: List<LessonTime>
) {
    fun toModel(): Lesson {
        return Lesson(
            `class` = `class`.toModel(),
            lessonNumber = lesson.lessonNumber,
            originalSubject = defaultLesson?.defaultLesson?.subject,
            changedSubject = lesson.changedSubject,
            teachers = teachers.map { it.acronym },
            teacherIsChanged = teachers.map { it.teacherId }.sorted() != listOf(
                defaultLesson?.defaultLesson?.teacherId
            ),
            rooms = rooms.map { it.name },
            roomIsChanged = lesson.roomIsChanged,
            info = lesson.info,
            start = DateUtils.getLocalDateTimeFromLocalDateAndTimeString(
                lessonTimes.getOrElse(
                    lesson.lessonNumber
                ) {
                    es.jvbabi.vplanplus.util.LessonTime.fallbackTime(
                        `class`.`class`.classId,
                        lesson.lessonNumber
                    )
                }.start, lesson.day
            ),
            end = DateUtils.getLocalDateTimeFromLocalDateAndTimeString(
                lessonTimes.getOrElse(
                    lesson.lessonNumber
                ) {
                    es.jvbabi.vplanplus.util.LessonTime.fallbackTime(
                        `class`.`class`.classId,
                        lesson.lessonNumber
                    )
                }.end, lesson.day
            ),
            vpId = defaultLesson?.defaultLesson?.vpId
        )
    }
}

