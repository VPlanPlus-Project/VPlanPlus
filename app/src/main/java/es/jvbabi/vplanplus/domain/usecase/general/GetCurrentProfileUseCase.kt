package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.Keys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetCurrentProfileUseCase(
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(): Flow<Profile?> = flow {
        keyValueRepository.getFlow(Keys.ACTIVE_PROFILE).distinctUntilChanged().collect {
            if (it == null) emit(null)
            else profileRepository.getProfileById(UUID.fromString(it)).distinctUntilChanged().collect { profile ->
                emit(profile)
            }
        }
    }
}