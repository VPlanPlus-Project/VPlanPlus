package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class GetHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke() = getCurrentProfileUseCase().flatMapLatest { profile ->
        if (profile is ClassProfile) homeworkRepository.getAllByProfile(profile)
        else flowOf(emptyList())
    }
}