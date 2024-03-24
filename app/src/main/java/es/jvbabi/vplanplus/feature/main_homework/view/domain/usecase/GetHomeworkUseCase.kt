package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
) {
    operator fun invoke() = flow {
        combine(
            homeworkRepository.getAll(),
            getCurrentIdentityUseCase()
        ) { homework, identity ->
            HomeworkResult(
                homework = homework.filter { it.classes.classId == identity?.profile?.referenceId },
                wrongProfile = identity?.profile?.type != ProfileType.STUDENT
            )
        }.collect {
            emit(it)
        }
    }
}

data class HomeworkResult(
    val homework: List<Homework>,
    val wrongProfile: Boolean = false
)