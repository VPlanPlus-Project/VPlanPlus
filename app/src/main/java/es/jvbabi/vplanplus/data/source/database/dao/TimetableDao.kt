package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbTimetable
import java.util.UUID

@Dao
abstract class TimetableDao {

    @Upsert
    abstract fun upsertTimetable(timetable: DbTimetable)

    @Query("INSERT OR REPLACE INTO timetable_room_crossover (lesson_id, room_id) VALUES (:lessonId, :roomId)")
    abstract fun upsertTimetableRoomCrossover(lessonId: UUID, roomId: Int)

    @Query("INSERT OR REPLACE INTO timetable_teacher_crossover (lesson_id, school_entity_id) VALUES (:lessonId, :schoolEntityId)")
    abstract fun upsertLessonTeacherCrossover(lessonId: UUID, schoolEntityId: UUID)

    @Query("DELETE FROM timetable WHERE class_id IN (SELECT id FROM `group` WHERE school_id = :schoolId)")
    abstract fun clearTimetableForSchool(schoolId: Int)
}