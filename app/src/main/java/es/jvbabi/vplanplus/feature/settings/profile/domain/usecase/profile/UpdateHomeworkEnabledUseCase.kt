package es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class UpdateHomeworkEnabledUseCase(
    private val profileRepository: ProfileRepository,
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(profile: ClassProfile, enabled: Boolean) {
        profileRepository.setHomeworkEnabled(profile, enabled)
        TODO()
//        if (!enabled) homeworkRepository.getAll().first().filter { (it as? CloudHomework)?..id == profile.id }.forEach { homeworkRepository.removeOrHideHomework(profile, it, DeleteTask.FORCE_DELETE_LOCALLY) }
    }
}