package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson

@Dao
abstract class ProfileDefaultLessonsCrossoverDao {

    @Query("DELETE FROM profile_default_lesson WHERE profileId = :profileId")
    abstract suspend fun deleteCrossoversByProfileId(profileId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCrossover(crossover: DbProfileDefaultLesson)
}