package es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.DeleteTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class UpdateHomeworkEnabledUseCase(
    private val profileRepository: ProfileRepository,
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(profile: Profile, enabled: Boolean) {
        profileRepository.setHomeworkEnabled(profile, enabled)
        if (!enabled) homeworkRepository.getAll().first().filter { it.profile.id == profile.id }.forEach { homeworkRepository.removeOrHideHomework(it, DeleteTask.FORCE_DELETE_LOCALLY) }
    }
}