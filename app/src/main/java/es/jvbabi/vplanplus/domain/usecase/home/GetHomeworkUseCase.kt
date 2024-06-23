package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.flow

class GetHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    operator fun invoke(profile: Profile?) = flow {
        if (profile !is ClassProfile) {
            emit(emptyList())
            return@flow
        }
        homeworkRepository.getHomeworkByGroupId(profile.group.groupId).collect { homework ->
            if (homeworkRepository.isUpdateRunning()) {
                return@collect
            }
            emit(homework)
        }
    }
}