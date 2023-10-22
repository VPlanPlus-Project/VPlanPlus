package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfiles(): Flow<List<Profile>>
}