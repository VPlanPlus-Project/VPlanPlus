package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase.DoesSchoolIdExistsUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase.IsSchoolIdValidUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase.OnboardingSchoolIdUseCases
import es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase.SetSchoolIdUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingSchoolIdModule {

    @Provides
    @Singleton
    fun provideOnboardingSchoolIdUseCases(
        schoolRepository: SchoolRepository,
        keyValueRepository: KeyValueRepository
    ) = OnboardingSchoolIdUseCases(
        isSchoolIdValidUseCase = IsSchoolIdValidUseCase(),
        doesSchoolIdExistsUseCase = DoesSchoolIdExistsUseCase(schoolRepository),
        setSchoolIdUseCase = SetSchoolIdUseCase(keyValueRepository, schoolRepository)
    )
}