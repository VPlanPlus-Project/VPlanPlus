package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first

class GetDefaultLessonsUseCase(
    private val defaultLessonRepository: DefaultLessonRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
) {

    suspend operator fun invoke(): Set<DefaultLesson> {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return emptySet()
        return defaultLessonRepository.getDefaultLessonByGroupId(profile.group.groupId).toSet()
    }
}