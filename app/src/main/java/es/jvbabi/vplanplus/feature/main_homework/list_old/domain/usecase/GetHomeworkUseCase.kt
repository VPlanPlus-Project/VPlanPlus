package es.jvbabi.vplanplus.feature.main_homework.list_old.domain.usecase

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
            if (profile !is ClassProfile) HomeworkResult(emptyList(), true)
            else HomeworkResult(
                homework = homework.filter { (it is Homework.CloudHomework && it.createdBy.group?.groupId == profile.group.groupId) || (it is Homework.LocalHomework && it.profile.id == profile.id) },
                wrongProfile = false
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