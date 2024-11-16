package es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class HasProfileLocalHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() = getCurrentProfileUseCase().flatMapLatest { profile ->
        if (profile !is ClassProfile) return@flatMapLatest flowOf(false)
        homeworkRepository.getAllByProfile(profile).map { homework -> homework.any { it is PersonalizedHomework.LocalHomework } }
    }
}