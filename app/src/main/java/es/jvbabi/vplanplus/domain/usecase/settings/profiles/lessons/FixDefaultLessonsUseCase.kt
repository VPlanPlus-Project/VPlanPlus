package es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class FixDefaultLessonsUseCase(
    private val profileRepository: ProfileRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val changeDefaultLessonUseCase: ChangeDefaultLessonUseCase
) {
    suspend operator fun invoke(profile: Profile) {
        if (profile.type != ProfileType.STUDENT) return
        profileRepository.deleteDefaultLessonsFromProfile(profile.id)
        defaultLessonRepository.getDefaultLessonByClassId(profile.referenceId).forEach { dl ->
            changeDefaultLessonUseCase(profile, dl, true)
        }
    }
}