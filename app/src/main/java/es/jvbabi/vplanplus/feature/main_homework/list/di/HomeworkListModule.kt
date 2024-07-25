package es.jvbabi.vplanplus.feature.main_homework.list.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsBalloonUseCase
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.DeleteHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.ToggleHomeworkHiddenStateUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.HomeworkListUseCases
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.MarkHomeworkAsDoneUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.SetHomeworkEnabledUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.UpdateHomeworkUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkListModule {

    @Provides
    @Singleton
    fun provideHomeworkListUseCases(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        updateHomeworkUseCase: UpdateHomeworkUseCase,
        homeworkRepository: HomeworkRepository,
        fileRepository: FileRepository,
        keyValueRepository: KeyValueRepository,
        profileRepository: ProfileRepository
    ) = HomeworkListUseCases(
        getCurrentProfileUseCase = getCurrentProfileUseCase,
        getHomeworkUseCase = GetHomeworkUseCase(
            getCurrentProfileUseCase = getCurrentProfileUseCase,
            homeworkRepository = homeworkRepository
        ),
        deleteHomeworkUseCase = DeleteHomeworkUseCase(
            homeworkRepository = homeworkRepository,
            fileRepository = fileRepository
        ),
        toggleHomeworkHiddenStateUseCase = ToggleHomeworkHiddenStateUseCase(
            homeworkRepository = homeworkRepository
        ),
        markHomeworkAsDoneUseCase = MarkHomeworkAsDoneUseCase(
            homeworkRepository = homeworkRepository
        ),
        updateHomeworkUseCase = updateHomeworkUseCase,
        isBalloonUseCase = IsBalloonUseCase(keyValueRepository),
        setBalloonUseCase = SetBalloonUseCase(keyValueRepository),
        setHomeworkEnabledUseCase = SetHomeworkEnabledUseCase(
            profileRepository = profileRepository
        )
    )
}