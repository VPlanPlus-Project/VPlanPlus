package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.source.database.crossover.LessonRoomCrossover
import es.jvbabi.vplanplus.data.source.database.crossover.LessonTeacherCrossover
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.LessonTime

data class CLesson(
    @Embedded val lesson: DbLesson,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = DbGroup::class
    ) val `class`: CGroup,
    @Relation(
        parentColumn = "default_lesson_id",
        entityColumn = "id",
        entity = DbDefaultLesson::class
    ) val defaultLessons: List<CDefaultLesson?>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = LessonTeacherCrossover::class,
            parentColumn = "lesson_id",
            entityColumn = "school_entity_id"
        ),
        entity = DbSchoolEntity::class
    )
    val teachers: List<CSchoolEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = LessonRoomCrossover::class,
            parentColumn = "lesson_id",
            entityColumn = "room_id"
        ),
        entity = DbRoom::class
    ) val rooms: List<CRoom>,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "group_id",
        entity = LessonTime::class
    ) val lessonTimes: List<LessonTime>,
    @Relation(
        parentColumn = "room_booking_id",
        entityColumn = "id",
        entity = DbRoomBooking::class
    ) val roomBooking: CRoomBooking?
) {
    fun toModel(): Lesson {
        val defaultLesson = defaultLessons.firstOrNull {
            it?.`class`?.group?.id == `class`.group.id
        }
        return Lesson(
            `class` = `class`.toModel(),
            lessonNumber = lesson.lessonNumber,
            originalSubject = defaultLesson?.defaultLesson?.subject,
            changedSubject = lesson.changedSubject,
            teachers = teachers.map { it.toTeacherModel() },
            teacherIsChanged = teachers.map { it.toTeacherModel().teacherId }.sorted() != listOf(defaultLesson?.defaultLesson?.teacherId),
            rooms = rooms.map { it.toModel() },
            roomIsChanged = lesson.isRoomChanged,
            info = lesson.info,
            start =
                (
                    lessonTimes.firstOrNull { it.lessonNumber == lesson.lessonNumber }?.start ?:
                    es.jvbabi.vplanplus.util.LessonTime.fallbackTime(`class`.group.id, lesson.lessonNumber)
                    .start
                )
                .withYear(lesson.day.year).withDayOfYear(lesson.day.dayOfYear),
            end =
                (
                    lessonTimes.firstOrNull { it.lessonNumber == lesson.lessonNumber }?.end ?:
                    es.jvbabi.vplanplus.util.LessonTime.fallbackTime(`class`.group.id, lesson.lessonNumber)
                    .end
                ).withYear(lesson.day.year).withDayOfYear(lesson.day.dayOfYear),
            defaultLesson = defaultLesson?.toModel(),
            roomBooking = roomBooking?.toModel()
        )
    }
}

