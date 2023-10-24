package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfiles(): Flow<List<Profile>>
    suspend fun createProfile(
        referenceId: Int,
        type: Int,
        name: String
    )

    suspend fun getClassesOnline(username: String, password: String, schoolId: String): List<Classes>
}