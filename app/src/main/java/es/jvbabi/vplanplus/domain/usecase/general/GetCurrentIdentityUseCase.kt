package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetCurrentIdentityUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke() = flow {
        keyValueRepository.getFlow(Keys.ACTIVE_PROFILE).collect {
            val profile = profileRepository.getProfileById(UUID.fromString(it ?: run { emit(null); return@collect })).first() ?: run { emit(null); return@collect }
            emit(Identity(
                school = profileRepository.getSchoolFromProfile(profile),
                profile = profile,
            ))
        }
    }
}

data class Identity(
    val school: School? = null,
    val profile: Profile? = null
)