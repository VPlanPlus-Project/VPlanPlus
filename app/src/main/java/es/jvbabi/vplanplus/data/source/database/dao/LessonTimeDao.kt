package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.domain.model.LessonTime
import java.util.UUID

@Dao
abstract class LessonTimeDao {
    @Insert
    abstract suspend fun insertLessonTime(lessonTime: LessonTime)

    @Query("DELETE FROM lesson_time WHERE classLessonTimeRefId = :classId")
    abstract suspend fun deleteLessonTimes(classId: UUID)

    @Query("SELECT * FROM lesson_time WHERE classLessonTimeRefId = :classId")
    abstract suspend fun getLessonTimesByClassId(classId: UUID): List<LessonTime>

    @Transaction
    open suspend fun insertLessonTimes(lessonTimes: List<LessonTime>) {
        lessonTimes.forEach {
            insertLessonTime(it)
        }
    }
}