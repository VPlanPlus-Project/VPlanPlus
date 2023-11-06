package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType

@Dao
abstract class ProfileDao {
    @Query("SELECT * FROM profile")
    abstract suspend fun getProfiles(): List<Profile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(profile: Profile)

    @Query("SELECT * FROM profile WHERE referenceId = :referenceId AND type = :type")
    abstract suspend fun getProfileByReferenceId(referenceId: Long, type: ProfileType): Profile

    @Query("SELECT * FROM profile WHERE id = :id")
    abstract suspend fun getProfileById(id: Long): Profile

    @Query("DELETE FROM profile WHERE id = :profileId")
    abstract suspend fun deleteProfile(profileId: Long)

    @Query("SELECT * FROM profile WHERE referenceId IN (SELECT id FROM classes WHERE schoolId = :schoolId) OR referenceId IN (SELECT id FROM teacher WHERE schoolId = :schoolId) OR referenceId IN (SELECT id FROM room WHERE schoolId = :schoolId)")
    abstract suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile>
}