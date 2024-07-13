package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class SetHomeworkEnabledUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: ClassProfile) {
        profileRepository.setHomeworkEnabled(profile, true)
    }
}