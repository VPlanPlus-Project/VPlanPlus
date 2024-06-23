package es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.IsFirstProfileForSchoolUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.OnboardingProfileTypeUseCases
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.SetProfileTypeUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingProfileTypeModule {

    @Provides
    @Singleton
    fun provideOnboardingProfileTypeUseCases(
        keyValueRepository: KeyValueRepository
    ) = OnboardingProfileTypeUseCases(
        isFirstProfileForSchoolUseCase = IsFirstProfileForSchoolUseCase(keyValueRepository),
        setProfileTypeUseCase = SetProfileTypeUseCase(keyValueRepository)
    )
}