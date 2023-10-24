package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class OnboardingUseCases(
    private val profileRepository: ProfileRepository
) {
    suspend fun getClassesOnlineStudent(
        schoolId: String,
        username: String,
        password: String,
    ): List<Classes> {
        return profileRepository.getClassesOnline(
            username = username,
            password = password,
            schoolId = schoolId
        )
    }
}