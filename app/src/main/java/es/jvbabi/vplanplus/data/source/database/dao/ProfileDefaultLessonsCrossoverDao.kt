package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class ProfileDefaultLessonsCrossoverDao {

    @Query("DELETE FROM profile_default_lesson WHERE profile_id = :profileId")
    abstract suspend fun deleteCrossoversByProfileId(profileId: UUID)

    @Query("DELETE FROM profile_default_lesson WHERE vp_id = :defaultLessonVpId")
    abstract suspend fun deleteCrossoversByDefaultLessonVpId(defaultLessonVpId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCrossover(crossover: DbProfileDefaultLesson)
}