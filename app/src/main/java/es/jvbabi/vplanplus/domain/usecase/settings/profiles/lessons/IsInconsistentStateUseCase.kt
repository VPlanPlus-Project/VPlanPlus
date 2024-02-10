package es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase

class IsInconsistentStateUseCase(
    private val defaultLessonRepository: DefaultLessonRepository,
    private val getClassByProfileUseCase: GetClassByProfileUseCase
) {
    suspend operator fun invoke(profile: Profile): Boolean {
        if (profile.type != ProfileType.STUDENT) return false
        return profile.defaultLessons.size != defaultLessonRepository.getDefaultLessonByClassId(
            getClassByProfileUseCase(profile)?.classId ?: return false
        ).size
    }
}