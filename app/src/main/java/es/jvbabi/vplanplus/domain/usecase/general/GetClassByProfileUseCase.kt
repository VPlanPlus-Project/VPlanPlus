package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ClassRepository

class GetClassByProfileUseCase(
    private val classRepository: ClassRepository
) {
    suspend operator fun invoke(profile: Profile) = classRepository.getClassById(profile.referenceId)
}