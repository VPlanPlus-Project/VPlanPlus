package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.LessonTime

@Dao
abstract class LessonTimeDao {
    @Insert
    abstract suspend fun insertLessonTime(lessonTime: LessonTime)

    @Query("DELETE FROM lesson_time WHERE classId = :classId")
    abstract suspend fun deleteLessonTimes(classId: Int)
}