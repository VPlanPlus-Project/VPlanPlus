package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.flow

class GetHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    operator fun invoke(profile: Profile?) = flow {
        if (profile?.type != ProfileType.STUDENT) {
            emit(emptyList())
            return@flow
        }
        homeworkRepository.getHomeworkByClassId(profile.referenceId).collect { homework ->
            if (homeworkRepository.isUpdateRunning()) {
                return@collect
            }
            emit(homework)
        }
    }
}