package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.LessonTime

@Dao
abstract class LessonTimeDao {
    @Insert
    abstract suspend fun insertLessonTime(lessonTime: LessonTime)

    @Query("DELETE FROM lesson_time WHERE group_id = :groupId")
    abstract suspend fun deleteLessonTimes(groupId: Int)

    @Query("SELECT * FROM lesson_time WHERE group_id = :groupId")
    abstract suspend fun getLessonTimesByGroupId(groupId: Int): List<LessonTime>
}