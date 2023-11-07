package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Profile
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProfileDao {
    @Query("SELECT * FROM profile")
    abstract fun getProfiles(): Flow<List<Profile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(profile: Profile)

    @Query("SELECT * FROM profile WHERE referenceId = :referenceId AND type = :type")
    abstract suspend fun getProfileByReferenceId(referenceId: Long, type: Int): Profile

    @Query("SELECT * FROM profile WHERE id = :id")
    abstract suspend fun getProfileById(id: Long): Profile
}