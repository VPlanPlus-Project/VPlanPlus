package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetCurrentIdentityUseCase(
    private val vppIdRepository: VppIdRepository,
    private val classRepository: ClassRepository,
    private val keyValueRepository: KeyValueRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke() = flow {
        combine(
            keyValueRepository.getFlow(Keys.ACTIVE_PROFILE),
            vppIdRepository.getVppIds(),
        ) { activeProfileId, vppIds ->
            if (activeProfileId == null) {
                return@combine null
            }
            val profile = profileRepository.getProfileById(UUID.fromString(activeProfileId)).first()
                ?: return@combine null
            val vppId = vppIds.firstOrNull { vppId ->
                vppId.classes == classRepository.getClassById(profile.referenceId) && vppId.isActive()
            }
            Identity(
                school = profileRepository.getSchoolFromProfile(profile),
                profile = profile,
                vppId = vppId
            )
        }.collect {
            emit(it)
        }
    }
}

data class Identity(
    val school: School? = null,
    val profile: Profile? = null,
    val vppId: VppId? = null
)