package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    @Suppress("UNCHECKED_CAST")
    operator fun invoke() = flow {
        combine(
            listOf(
                homeworkRepository.getAll(),
                getCurrentProfileUseCase()
            )
        ) { data ->
            val homework = data[0] as List<Homework>
            val profile = data[1] as? ClassProfile ?: return@combine emptyList()
            homework.filter { (it is Homework.LocalHomework && it.profile.id == profile.id) || (it is Homework.CloudHomework && it.createdBy.group?.groupId == profile.group.groupId) }
        }.collect {
            emit(it)
        }
    }
}