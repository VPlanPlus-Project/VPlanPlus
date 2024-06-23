package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
) {
    operator fun invoke() = flow {
        combine(
            homeworkRepository.getAll(),
            getCurrentProfileUseCase()
        ) { homework, profile ->
            HomeworkResult(
                homework = homework.filter { it.profile == profile },
                wrongProfile = profile !is ClassProfile
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