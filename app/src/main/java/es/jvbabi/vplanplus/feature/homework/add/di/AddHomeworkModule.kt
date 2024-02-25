package es.jvbabi.vplanplus.feature.homework.add.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.homework.add.domain.usecase.AddHomeworkUseCases
import es.jvbabi.vplanplus.feature.homework.add.domain.usecase.CanShowVppIdBannerUseCase
import es.jvbabi.vplanplus.feature.homework.add.domain.usecase.GetDefaultLessonsUseCase
import es.jvbabi.vplanplus.feature.homework.add.domain.usecase.HideVppIdBannerUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddHomeworkModule {

    @Provides
    @Singleton
    fun provideAddHomeworkUseCases(
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
        getClassByProfileUseCase: GetClassByProfileUseCase,
        defaultLessonRepository: DefaultLessonRepository,
        keyValueRepository: KeyValueRepository
    ): AddHomeworkUseCases {
        return AddHomeworkUseCases(
            getDefaultLessonsUseCase = GetDefaultLessonsUseCase(
                defaultLessonRepository = defaultLessonRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase,
                getClassByProfileUseCase = getClassByProfileUseCase
            ),
            canShowVppIdBannerUseCase = CanShowVppIdBannerUseCase(
                keyValueRepository = keyValueRepository
            ),
            hideVppIdBannerUseCase = HideVppIdBannerUseCase(
                keyValueRepository = keyValueRepository
            )
        )
    }
}