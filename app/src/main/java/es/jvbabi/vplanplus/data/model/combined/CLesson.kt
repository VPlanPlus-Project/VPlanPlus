package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbLesson
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
    ) val defaultLesson: CDefaultLesson?,
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
    ) val lessonTimes: List<LessonTime>
) {
    fun toModel(): Lesson {
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
                lessonTimes.getOrElse(
                    lesson.lessonNumber
                ) {
                    es.jvbabi.vplanplus.util.LessonTime.fallbackTime(
                        `class`.schoolEntity.id,
                        lesson.lessonNumber
                    )
                }.start.withDayOfYear(lesson.day.dayOfYear).withYear(lesson.day.year),
            end =
                lessonTimes.getOrElse(
                    lesson.lessonNumber
                ) {
                    es.jvbabi.vplanplus.util.LessonTime.fallbackTime(
                        `class`.schoolEntity.id,
                        lesson.lessonNumber
                    )
                }.end.withDayOfYear(lesson.day.dayOfYear).withYear(lesson.day.year),
            vpId = defaultLesson?.defaultLesson?.vpId
        )
    }
}

