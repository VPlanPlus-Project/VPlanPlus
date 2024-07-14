package es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class UpdateHomeworkEnabledUseCase(
    private val profileRepository: ProfileRepository,
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(profile: ClassProfile, enabled: Boolean) {
        profileRepository.setHomeworkEnabled(profile, enabled)
        if (!enabled) homeworkRepository.getAllByProfile(profile).first().forEach { homeworkRepository.deleteHomeworkDb(it.homework) }
    }
}