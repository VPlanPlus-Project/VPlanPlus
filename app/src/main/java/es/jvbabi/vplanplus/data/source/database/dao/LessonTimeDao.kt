package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.LessonTime

@Dao
abstract class LessonTimeDao {
    @Insert
    abstract suspend fun insertLessonTime(lessonTime: LessonTime)

    @Query("DELETE FROM lesson_time WHERE classLessonTimeRefId = :classId")
    abstract suspend fun deleteLessonTimes(classId: Long)

    @Query("SELECT * FROM lesson_time WHERE classLessonTimeRefId = :classId")
    abstract suspend fun getLessonTimesByClassId(classId: Long): List<LessonTime>
}