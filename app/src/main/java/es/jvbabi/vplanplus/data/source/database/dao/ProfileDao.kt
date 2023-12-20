package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.model.combined.CProfile
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class ProfileDao {
    @Query("SELECT * FROM profile")
    @Transaction
    abstract fun getProfiles(): Flow<List<CProfile>>

    @Upsert
    abstract suspend fun insert(profile: DbProfile)

    @Query("SELECT * FROM profile WHERE referenceId = :referenceId AND type = :type")
    @Transaction
    abstract suspend fun getProfileByReferenceId(referenceId: UUID, type: ProfileType): CProfile

    @Query("SELECT * FROM profile WHERE profileId = :id")
    @Transaction
    abstract fun getProfileById(id: UUID): Flow<CProfile?>

    @Query("DELETE FROM profile WHERE profileId = :profileId")
    abstract suspend fun deleteProfile(profileId: UUID)

    @Query("SELECT * FROM profile WHERE (type = 1 AND referenceId IN (SELECT id FROM school_entity WHERE schoolId = :schoolId))")
    @Transaction
    abstract suspend fun getProfilesBySchoolId(schoolId: Long): List<CProfile>
}