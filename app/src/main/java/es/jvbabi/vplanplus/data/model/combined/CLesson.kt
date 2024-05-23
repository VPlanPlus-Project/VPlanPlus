package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.data.source.database.crossover.LessonSchoolEntityCrossover
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.LessonTime

data class CLesson(
    @Embedded val lesson: DbLesson,
    @Relation(
        parentColumn = "classLessonRefId", entityColumn = "id", entity = DbSchoolEntity::class
    ) val `class`: CSchoolEntity,
    @Relation(
        parentColumn = "defaultLessonId",
        entityColumn = "defaultLessonId",
        entity = DbDefaultLesson::class
    ) val defaultLessons: List<CDefaultLesson?>,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "id",
        associateBy = Junction(
            value = LessonSchoolEntityCrossover::class,
            parentColumn = "lsecLessonId",
            entityColumn = "lsecSchoolEntityId"
        ),
        entity = DbSchoolEntity::class
    )
    val schoolEntities: List<CSchoolEntity>,
    @Relation(
        parentColumn = "classLessonRefId",
        entityColumn = "classLessonTimeRefId",
        entity = LessonTime::class
    ) val lessonTimes: List<LessonTime>,
    @Relation(
        parentColumn = "roomBookingId",
        entityColumn = "roomId",
        entity = DbRoomBooking::class
    ) val roomBooking: CRoomBooking?
) {
    fun toModel(): Lesson {
        val defaultLesson = defaultLessons.firstOrNull {
            it?.`class`?.schoolEntity?.id == `class`.schoolEntity.id
        }
        return Lesson(
            `class` = `class`.toClassModel(),
            lessonNumber = lesson.lessonNumber,
            originalSubject = defaultLesson?.defaultLesson?.subject,
            changedSubject = lesson.changedSubject,
            teachers = schoolEntities.filter { it.schoolEntity.type == SchoolEntityType.TEACHER }.map { it.toTeacherModel().acronym },
            teacherIsChanged = schoolEntities.filter { it.schoolEntity.type == SchoolEntityType.TEACHER }.map { it.toTeacherModel().teacherId }.sorted() != listOf(
                defaultLesson?.defaultLesson?.teacherId
            ),
            rooms = schoolEntities.filter { it.schoolEntity.type == SchoolEntityType.ROOM }.map { it.toRoomModel().name },
            roomIsChanged = lesson.roomIsChanged,
            info = lesson.info,
            start =
                (
                    lessonTimes.firstOrNull { it.lessonNumber == lesson.lessonNumber }?.start ?:
                    es.jvbabi.vplanplus.util.LessonTime.fallbackTime(`class`.schoolEntity.id, lesson.lessonNumber)
                    .start
                )
                .withYear(lesson.day.year).withDayOfYear(lesson.day.dayOfYear),
            end =
                (
                    lessonTimes.firstOrNull { it.lessonNumber == lesson.lessonNumber }?.end ?:
                    es.jvbabi.vplanplus.util.LessonTime.fallbackTime(`class`.schoolEntity.id, lesson.lessonNumber)
                    .end
                ).withYear(lesson.day.year).withDayOfYear(lesson.day.dayOfYear),
            vpId = defaultLesson?.defaultLesson?.vpId,
            roomBooking = roomBooking?.toModel()
        )
    }
}

