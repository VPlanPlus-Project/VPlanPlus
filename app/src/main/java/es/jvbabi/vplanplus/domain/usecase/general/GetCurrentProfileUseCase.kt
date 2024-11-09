package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class GetCurrentProfileUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val profileRepository: ProfileRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() = keyValueRepository.getFlow(Keys.ACTIVE_PROFILE).flatMapLatest { profileIdString ->
            val profileId = try {
                UUID.fromString(profileIdString)
            } catch (e: IllegalArgumentException) {
                null
            }
            if (profileId == null) return@flatMapLatest flowOf(null)
            return@flatMapLatest profileRepository.getProfileById(profileId)
        }
}