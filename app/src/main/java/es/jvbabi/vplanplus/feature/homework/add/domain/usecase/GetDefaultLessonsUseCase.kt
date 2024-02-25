package es.jvbabi.vplanplus.feature.homework.add.domain.usecase

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import kotlinx.coroutines.flow.first

class GetDefaultLessonsUseCase(
    private val defaultLessonRepository: DefaultLessonRepository,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val getClassByProfileUseCase: GetClassByProfileUseCase
) {

    suspend operator fun invoke(): List<DefaultLesson> {
        val profile = getCurrentIdentityUseCase().first()?.profile ?: return emptyList()
        val `class` = getClassByProfileUseCase(profile) ?: return emptyList()
        return defaultLessonRepository.getDefaultLessonByClassId(`class`.classId)
    }
}