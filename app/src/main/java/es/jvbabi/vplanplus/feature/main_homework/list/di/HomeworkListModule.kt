package es.jvbabi.vplanplus.feature.main_homework.list.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.DeleteHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.ToggleHomeworkHiddenStateUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.HomeworkListUseCases
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.MarkHomeworkAsDoneUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkListModule {

    @Provides
    @Singleton
    fun provideHomeworkListUseCases(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        homeworkRepository: HomeworkRepository,
        fileRepository: FileRepository
    ) = HomeworkListUseCases(
        getCurrentProfileUseCase = getCurrentProfileUseCase,
        getHomeworkUseCase = GetHomeworkUseCase(
            getCurrentProfileUseCase = getCurrentProfileUseCase,
            homeworkRepository = homeworkRepository
        ),
        deleteHomeworkUseCase = DeleteHomeworkUseCase(
            homeworkRepository = homeworkRepository,
            fileRepository = fileRepository,
            getCurrentProfileUseCase = getCurrentProfileUseCase
        ),
        toggleHomeworkHiddenStateUseCase = ToggleHomeworkHiddenStateUseCase(
            homeworkRepository = homeworkRepository
        ),
        markHomeworkAsDoneUseCase = MarkHomeworkAsDoneUseCase(
            homeworkRepository = homeworkRepository,
            getCurrentProfileUseCase = getCurrentProfileUseCase
        )
    )
}