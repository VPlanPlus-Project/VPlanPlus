package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfiles(): Flow<List<Profile>>
    suspend fun createProfile(referenceId: Long, type: Int, name: String)
    suspend fun getClassesOnline(username: String, password: String, schoolId: Long): List<Classes>
    suspend fun getProfileByReferenceId(referenceId: Long, type: Int): Profile
    suspend fun getProfileById(id: Long): Profile
}