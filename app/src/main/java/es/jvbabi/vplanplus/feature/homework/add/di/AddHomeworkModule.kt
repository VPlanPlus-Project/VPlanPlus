package es.jvbabi.vplanplus.feature.homework.add.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.homework.add.domain.AddHomeworkUseCases
import es.jvbabi.vplanplus.feature.homework.add.domain.GetDefaultLessonsUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddHomeworkModule {

    @Provides
    @Singleton
    fun provideAddHomeworkUseCases(
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
        getClassByProfileUseCase: GetClassByProfileUseCase,
        defaultLessonRepository: DefaultLessonRepository
    ): AddHomeworkUseCases {
        return AddHomeworkUseCases(
            getDefaultLessonsUseCase = GetDefaultLessonsUseCase(
                defaultLessonRepository = defaultLessonRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase,
                getClassByProfileUseCase = getClassByProfileUseCase
            ),
        )
    }
}