package es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository

class IsInconsistentStateUseCase(
    private val defaultLessonRepository: DefaultLessonRepository,
) {
    suspend operator fun invoke(profile: Profile): Boolean {
        if (profile !is ClassProfile) return false
        return profile.defaultLessons.size != defaultLessonRepository.getDefaultLessonByGroupId(profile.group.groupId).size
    }
}