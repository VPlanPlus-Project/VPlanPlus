package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetCurrentIdentityUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke() = flow {
        combine(
            keyValueRepository.getFlow(Keys.ACTIVE_PROFILE),
            profileRepository.getProfiles()
        ) { identityId, profiles ->
            if (profiles.isEmpty() || identityId == null) return@combine null
            val uuid = try {
                UUID.fromString(identityId)
            } catch (e: IllegalArgumentException) {return@combine null }
            val profile = profiles.firstOrNull { it.id == uuid } ?: return@combine null
            Identity(
                school = profileRepository.getSchoolFromProfile(profile),
                profile = profile,
            )
        }.collect { identity ->
            emit(identity)
        }
    }
}

data class Identity(
    val school: School? = null,
    val profile: Profile? = null
)