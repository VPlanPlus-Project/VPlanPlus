package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class ProfileSelectedDefaultLessonCrossoverDao {

    @Query("SELECT psdlcDefaultLessonId FROM profile_selected_default_lesson_crossover WHERE psdlcProfileId = :profileId")
    abstract suspend fun getDefaultLessonIdsByProfileId(profileId: Long): List<Long>

    @Query("DELETE FROM profile_selected_default_lesson_crossover WHERE psdlcProfileId = :profileId")
    abstract suspend fun deleteCrossoversByProfileId(profileId: Long)

    @Query("INSERT INTO profile_selected_default_lesson_crossover (psdlcProfileId, psdlcDefaultLessonId) VALUES (:profileId, :defaultLessonId)")
    abstract suspend fun insertCrossover(profileId: Long, defaultLessonId: Long)
}