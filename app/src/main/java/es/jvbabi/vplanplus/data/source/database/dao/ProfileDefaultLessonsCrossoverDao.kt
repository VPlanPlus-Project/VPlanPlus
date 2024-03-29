package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import java.util.UUID

@Dao
abstract class ProfileDefaultLessonsCrossoverDao {

    @Query("DELETE FROM profile_default_lesson WHERE profileId = :profileId")
    abstract suspend fun deleteCrossoversByProfileId(profileId: UUID)

    @Query("DELETE FROM profile_default_lesson WHERE defaultLessonVpId = :defaultLessonVpId")
    abstract suspend fun deleteCrossoversByDefaultLessonVpId(defaultLessonVpId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCrossover(crossover: DbProfileDefaultLesson)
}