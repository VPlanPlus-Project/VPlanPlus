package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class ProfileSelectedDefaultLessonCrossoverDao {

    @Query("DELETE FROM profile_selected_default_lesson_crossover WHERE psdlcProfileId = :profileId")
    abstract suspend fun deleteCrossoversByProfileId(profileId: Long)

    @Query("INSERT INTO profile_selected_default_lesson_crossover (psdlcProfileId, psdlcDefaultLessonVpId) VALUES (:profileId, :vpId)")
    abstract suspend fun insertCrossover(profileId: Long, vpId: Long)

    @Query("DELETE FROM profile_selected_default_lesson_crossover WHERE psdlcProfileId = :profileId AND psdlcDefaultLessonVpId = :vpId")
    abstract suspend fun deleteCrossover(profileId: Long, vpId: Long)
}