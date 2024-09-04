package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.DbTimetable
import es.jvbabi.vplanplus.data.model.DbWeek
import es.jvbabi.vplanplus.data.model.DbWeekType
import es.jvbabi.vplanplus.data.source.database.crossover.TimetableRoomCrossover
import es.jvbabi.vplanplus.data.source.database.crossover.TimetableTeacherCrossover
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbTeacher
import java.time.DayOfWeek

data class CTimetable(
    @Embedded val timetable: DbTimetable,
    @Relation(
        parentColumn = "class_id",
        entityColumn = "id",
        entity = DbGroup::class
    ) val group: CGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TimetableRoomCrossover::class,
            parentColumn = "lesson_id",
            entityColumn = "room_id"
        ),
        entity = DbRoom::class
    ) val rooms: List<CRoom>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TimetableTeacherCrossover::class,
            parentColumn = "lesson_id",
            entityColumn = "school_entity_id"
        ),
        entity = DbSchoolEntity::class
    ) val teachers: List<CSchoolEntity>,
    @Relation(
        parentColumn = "week_id",
        entityColumn = "id",
        entity = DbWeek::class
    ) val week: CWeek?,
    @Relation(
        parentColumn = "week_type_id",
        entityColumn = "id",
        entity = DbWeekType::class
    ) val weekType: CWeekType?
) {
    fun toModel(lessonTimes: List<LessonTime>): Lesson.TimetableLesson {
        return Lesson.TimetableLesson(
            id = timetable.id,
            group = group.toModel(),
            rooms = rooms.map { it.toModel() },
            teachers = teachers.map { it.toTeacherModel() },
            lessonNumber = timetable.lessonNumber,
            subject = timetable.subject,
            week = week?.toModel(),
            weekType = weekType?.toModel(),
            start = lessonTimes.firstOrNull { it.lessonNumber == timetable.lessonNumber && it.groupId == group.group.id }?.start!!,
            end = lessonTimes.firstOrNull { it.lessonNumber == timetable.lessonNumber && it.groupId == group.group.id }?.end!!,
        )
    }
}