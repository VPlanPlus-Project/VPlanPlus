package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbTimetable
import es.jvbabi.vplanplus.data.model.combined.CTimetable
import es.jvbabi.vplanplus.data.source.database.crossover.TimetableRoomCrossover
import es.jvbabi.vplanplus.data.source.database.crossover.TimetableTeacherCrossover
import java.util.UUID

@Dao
abstract class TimetableDao {

    @Upsert
    abstract fun upsertTimetable(timetable: DbTimetable)

    @Upsert
    abstract fun upsertTimetableLessons(timetable: List<DbTimetable>)

    @Query("INSERT OR REPLACE INTO timetable_room_crossover (lesson_id, room_id) VALUES (:lessonId, :roomId)")
    abstract fun upsertTimetableRoomCrossover(lessonId: UUID, roomId: Int)

    @Upsert
    abstract fun upsertTimetableRoomCrossovers(crossovers: List<TimetableRoomCrossover>)

    @Query("INSERT OR REPLACE INTO timetable_teacher_crossover (lesson_id, school_entity_id) VALUES (:lessonId, :schoolEntityId)")
    abstract fun upsertLessonTeacherCrossover(lessonId: UUID, schoolEntityId: UUID)

    @Upsert
    abstract fun upsertLessonTeacherCrossovers(crossovers: List<TimetableTeacherCrossover>)

    @Query("DELETE FROM timetable WHERE class_id IN (SELECT id FROM `group` WHERE school_id = :schoolId)")
    abstract fun clearTimetableForSchool(schoolId: Int)

    @Transaction
    @Query("SELECT * FROM timetable WHERE class_id = :groupId AND ((week_id = :weekId OR :weekId IS NULL OR week_id IS NULL) AND (week_type_id = :weekTypeId OR :weekTypeId IS NULL OR week_type_id IS NULL)) AND day_of_week = :dayOfWeek")
    abstract fun getTimetableForGroup(groupId: Int, weekId: Int?, weekTypeId: Int?, dayOfWeek: Int): List<CTimetable>

    @Transaction
    @Query("SELECT timetable.id AS id, timetable.class_id AS class_id, timetable.day_of_week AS day_of_week, timetable.week_id AS week_id, timetable.week_type_id AS week_type_id, timetable.lesson_number AS lesson_number, timetable.subject AS subject FROM timetable LEFT JOIN `group` ON `group`.id = timetable.class_id LEFT JOIN week ON week.id = timetable.week_id WHERE `group`.school_id = :schoolId AND ((week_id = :weekId OR :weekId IS NULL OR week_id IS NULL) AND (timetable.week_type_id = :weekTypeId OR :weekTypeId IS NULL OR timetable.week_type_id IS NULL OR week.week_type_id = timetable.week_type_id OR week.week_type_id = :weekTypeId))")
    abstract fun getWeekTimetableForSchool(schoolId: Int, weekId: Int?, weekTypeId: Int?): List<CTimetable>

    @Query("DELETE FROM timetable WHERE id IN (:ids)")
    abstract fun deleteFromTimetableById(ids: List<UUID>)

    @Query("DELETE FROM timetable")
    abstract suspend fun deleteAllTimetable()
}