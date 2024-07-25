package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class GetProfilesUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke() = flow {
        profileRepository.getProfiles().distinctUntilChanged().collect { profiles ->
            val profilesBySchool = mutableMapOf<School, List<Profile>>()
            profiles.forEach { profile ->
                val school = profile.getSchool()
                if (profilesBySchool.containsKey(school)) {
                    profilesBySchool[school] = profilesBySchool[school]!!.plus(profile)
                } else {
                    profilesBySchool[school] = listOf(profile)
                }
            }
            emit(profilesBySchool)
        }
    }
}