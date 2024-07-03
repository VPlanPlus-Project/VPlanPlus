package es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.LocalHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class HasProfileLocalHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(profile: ClassProfile): Boolean {
        return homeworkRepository.getAll().first().any { (it is LocalHomework && it.profile.id == profile.id) || (it is CloudHomework && it.createdBy.group?.groupId == profile.group.groupId) }
    }
}