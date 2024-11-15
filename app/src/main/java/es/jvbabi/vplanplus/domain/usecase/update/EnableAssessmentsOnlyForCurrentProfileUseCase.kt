package es.jvbabi.vplanplus.domain.usecase.update

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach
import java.util.UUID

class EnableAssessmentsOnlyForCurrentProfileUseCase(
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        val currentProfileId = keyValueRepository.get(Keys.ACTIVE_PROFILE)?.let {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        } ?: return
        profileRepository
            .getProfiles()
            .filterIsInstance<ClassProfile>()
            .filter { it.id == currentProfileId }
            .onEach {
                profileRepository.setAssessmentEnabled(it, true)
            }
    }
}