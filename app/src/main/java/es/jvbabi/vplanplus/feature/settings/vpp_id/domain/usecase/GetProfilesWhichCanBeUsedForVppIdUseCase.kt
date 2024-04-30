package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class GetProfilesWhichCanBeUsedForVppIdUseCase(
    private val profileRepository: ProfileRepository,
    private val classRepository: ClassRepository
) {

    suspend operator fun invoke(vppId: VppId): List<Profile> {
        val school = vppId.school ?: return emptyList()
        return profileRepository.getProfilesBySchoolId(school.schoolId)
            .filter { it.type == ProfileType.STUDENT }
            .filter { classRepository.getClassById(it.referenceId) == vppId.classes }
            .filter { it.vppId == vppId || it.vppId == null }
    }
}