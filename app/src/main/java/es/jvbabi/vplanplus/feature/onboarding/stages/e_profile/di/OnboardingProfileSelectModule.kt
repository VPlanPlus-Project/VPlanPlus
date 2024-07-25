package es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.IsFirstProfileForSchoolUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.domain.usecase.GetProfileOptionsUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.domain.usecase.OnboardingProfileSelectUseCases
import es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.domain.usecase.SetProfileUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingProfileSelectModule {

    @Provides
    @Singleton
    fun provideOnboardingProfileSelectUseCases(
        keyValueRepository: KeyValueRepository
    ) = OnboardingProfileSelectUseCases(
        isFirstProfileForSchoolUseCase = IsFirstProfileForSchoolUseCase(keyValueRepository),
        setProfileUseCase = SetProfileUseCase(keyValueRepository),
        getProfileOptionsUseCase = GetProfileOptionsUseCase(keyValueRepository)
    )
}