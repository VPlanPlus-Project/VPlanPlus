package es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.domain.usecase.GetDefaultLessonsUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.domain.usecase.OnboardingDefaultLessonsUseCases
import es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.domain.usecase.SetDefaultLessonsUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingDefaultLessonsModule {

    @Provides
    @Singleton
    fun provideOnboardingDefaultLessonsUseCases(
        keyValueRepository: KeyValueRepository
    ) = OnboardingDefaultLessonsUseCases(
        getDefaultLessonsUseCase = GetDefaultLessonsUseCase(keyValueRepository),
        setDefaultLessonsUseCase = SetDefaultLessonsUseCase(keyValueRepository)
    )
}