package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.model.combined.CProfile
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProfileDao {
    @Query("SELECT * FROM profile")
    abstract fun getProfiles(): Flow<List<CProfile>>

    @Upsert
    abstract suspend fun insert(profile: DbProfile): Long

    @Query("SELECT * FROM profile WHERE referenceId = :referenceId AND type = :type")
    abstract suspend fun getProfileByReferenceId(referenceId: Long, type: ProfileType): CProfile

    @Query("SELECT * FROM profile WHERE id = :id")
    abstract fun getProfileById(id: Long): Flow<CProfile>

    @Query("DELETE FROM profile WHERE id = :profileId")
    abstract suspend fun deleteProfile(profileId: Long)

    @Query("SELECT * FROM profile WHERE (type = 1 AND referenceId IN (SELECT classId FROM classes WHERE schoolClassRefId = :schoolId)) OR (type = 0 AND referenceId IN (SELECT teacherId FROM teacher WHERE schoolTeacherRefId = :schoolId)) OR (type = 2 AND referenceId IN (SELECT roomId FROM room WHERE schoolRoomRefId = :schoolId))\n")
    abstract suspend fun getProfilesBySchoolId(schoolId: Long): List<CProfile>
}