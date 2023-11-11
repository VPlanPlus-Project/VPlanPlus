package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfiles(): Flow<List<Profile>>
    suspend fun createProfile(referenceId: Long, type: ProfileType, name: String)
    suspend fun getClassesOnline(username: String, password: String, schoolId: Long): List<Classes>
    suspend fun getProfileByReferenceId(referenceId: Long, type: ProfileType): Profile
    suspend fun getProfileById(id: Long): Profile
    suspend fun deleteProfile(profileId: Long)
    suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile>
}