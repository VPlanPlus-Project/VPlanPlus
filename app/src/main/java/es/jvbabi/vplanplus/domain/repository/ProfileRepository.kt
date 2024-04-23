package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VppId
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProfileRepository {
    fun getProfiles(): Flow<List<Profile>>
    suspend fun createProfile(referenceId: UUID, type: ProfileType, name: String, customName: String): UUID
    suspend fun getProfileByReferenceId(referenceId: UUID, type: ProfileType): Profile
    fun getProfileById(id: UUID): Flow<Profile?>
    suspend fun deleteProfile(profileId: UUID)
    suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile>
    suspend fun updateProfile(profile: DbProfile)
    suspend fun getDbProfileById(profileId: UUID): DbProfile?

    suspend fun enableDefaultLesson(profileId: UUID, vpId: Long)
    suspend fun disableDefaultLesson(profileId: UUID, vpId: Long)
    suspend fun deleteDefaultLessonsFromProfile(profileId: UUID)
    suspend fun deleteDefaultLessonFromProfile(vpId: Long)

    suspend fun getSchoolFromProfile(profile: Profile): School
    suspend fun getActiveProfile(): Flow<Profile?>

    suspend fun setProfileVppId(profile: Profile, vppId: VppId?)
}