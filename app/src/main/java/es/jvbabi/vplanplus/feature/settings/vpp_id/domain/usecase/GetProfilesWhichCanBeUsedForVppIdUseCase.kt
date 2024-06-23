package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first

class GetProfilesWhichCanBeUsedForVppIdUseCase(
    private val profileRepository: ProfileRepository,
) {

    suspend operator fun invoke(vppId: VppId): List<ClassProfile> {
        val school = vppId.school ?: return emptyList()
        return profileRepository.getProfilesBySchool(school.id)
            .first()
            .filterIsInstance<ClassProfile>()
            .filter { it.group == vppId.group }
            .filter { it.vppId == vppId || it.vppId == null }
    }
}