package es.jvbabi.vplanplus.feature.main_homework.add.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsBalloonUseCase
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.AddHomeworkUseCases
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.GetDefaultLessonsUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.SaveHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddHomeworkModule {

    @Provides
    @Singleton
    fun provideAddHomeworkUseCases(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        defaultLessonRepository: DefaultLessonRepository,
        keyValueRepository: KeyValueRepository,
        homeworkRepository: HomeworkRepository,
        fileRepository: FileRepository
    ): AddHomeworkUseCases {
        return AddHomeworkUseCases(
            getDefaultLessonsUseCase = GetDefaultLessonsUseCase(
                defaultLessonRepository = defaultLessonRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase,
            ),
            saveHomeworkUseCase = SaveHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase,
                fileRepository = fileRepository
            ),
            isBalloonUseCase = IsBalloonUseCase(
                keyValueRepository = keyValueRepository,
            ),
            setBalloonUseCase = SetBalloonUseCase(
                keyValueRepository = keyValueRepository,
            ),
        )
    }
}