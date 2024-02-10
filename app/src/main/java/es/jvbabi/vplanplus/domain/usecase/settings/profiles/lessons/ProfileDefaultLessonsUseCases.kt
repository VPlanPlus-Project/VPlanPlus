package es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons

import es.jvbabi.vplanplus.domain.usecase.settings.profiles.shared.GetProfileByIdUseCase

data class ProfileDefaultLessonsUseCases(
    val getProfileByIdUseCase: GetProfileByIdUseCase,
    val isInconsistentStateUseCase: IsInconsistentStateUseCase,
    val changeDefaultLessonUseCase: ChangeDefaultLessonUseCase,
    val fixDefaultLessonsUseCase: FixDefaultLessonsUseCase
)