package es.jvbabi.vplanplus.feature.onboarding.stages.a_welcome.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.a_welcome.domain.usecase.OnboardingWelcomeUseCases

@Module
@InstallIn(SingletonComponent::class)
object OnboardingWelcomeModule {

    @Provides
    fun provideOnboardingWelcomeUseCases(
        keyValueRepository: KeyValueRepository
    ) = OnboardingWelcomeUseCases(GetVppIdServerUseCase(keyValueRepository))
}