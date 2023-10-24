package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileUseCases(
    private val profileRepository: ProfileRepository
) {

    fun atLeastOneProfileExists(): Flow<Boolean> {
        return profileRepository.getProfiles().map {
            profiles -> profiles.isNotEmpty()
        }
    }

    suspend fun createStudentProfile(classId: Int, name: String) {
        profileRepository.createProfile(referenceId = classId, type = 0, name = name)
    }
}