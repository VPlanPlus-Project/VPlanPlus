package es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.domain.usecase.OnboardingQrUseCases
import es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.domain.usecase.TestSchoolCredentialsUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.CheckCredentialsAndInitOnboardingForSchoolUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingQrModule {

    @Provides
    @Singleton
    fun provideOnboardingQrUseCases(
        schoolRepository: SchoolRepository,
        checkCredentialsAndInitOnboardingForSchoolUseCase: CheckCredentialsAndInitOnboardingForSchoolUseCase
    ) = OnboardingQrUseCases(
        testSchoolCredentialsUseCase = TestSchoolCredentialsUseCase(schoolRepository),
        checkCredentialsAndInitOnboardingForSchoolUseCase = checkCredentialsAndInitOnboardingForSchoolUseCase
    )
}