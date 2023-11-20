package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfiles(): Flow<List<Profile>>
    suspend fun createProfile(referenceId: Long, type: ProfileType, name: String, customName: String): Long
    suspend fun getProfileByReferenceId(referenceId: Long, type: ProfileType): Profile
    fun getProfileById(id: Long): Flow<Profile?>
    suspend fun deleteProfile(profileId: Long)
    suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile>
    suspend fun updateProfile(profile: DbProfile)
    suspend fun getDbProfileById(profileId: Long): DbProfile?

    suspend fun enableDefaultLesson(profileId: Long, vpId: Long)
    suspend fun disableDefaultLesson(profileId: Long, vpId: Long)
    suspend fun deleteDefaultLessonsFromProfile(profileId: Long)
}