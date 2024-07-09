package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetHomeworkUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke() = flow {
        combine(
            getCurrentProfileUseCase(),
            homeworkRepository.getAll()
        ) { profile, homework ->
            if (profile !is ClassProfile) return@combine emptyList()
            homework.filter { homeworkItem ->
                when (homeworkItem) {
                    is Homework.LocalHomework -> homeworkItem.profile == profile
                    is Homework.CloudHomework -> homeworkItem.createdBy.group?.groupId == profile.group.groupId
                }
            }
        }.collect {
            emit(it)
        }
    }
}