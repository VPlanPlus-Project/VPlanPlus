package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import es.jvbabi.vplanplus.domain.model.DefaultLesson

@Dao
abstract class DefaultLessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDefaultLesson(defaultLesson: DefaultLesson)
}