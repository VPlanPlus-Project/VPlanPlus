package es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.CheckCredentialsAndInitOnboardingForSchoolUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.GetSp24SchoolIdUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingCredentialsUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingCredentialsModule {

    @Provides
    @Singleton
    fun provideOnboardingCredentialsUseCases(
        checkCredentialsAndInitOnboardingForSchoolUseCase: CheckCredentialsAndInitOnboardingForSchoolUseCase,
        keyValueRepository: KeyValueRepository
    ) = OnboardingCredentialsUseCases(
        checkCredentialsAndInitOnboardingForSchoolUseCase = checkCredentialsAndInitOnboardingForSchoolUseCase,
        getSp24SchoolIdUseCase = GetSp24SchoolIdUseCase(keyValueRepository)
    )

    @Provides
    @Singleton
    fun provideCheckCredentialsAndInitOnboardingForSchoolUseCase(
        schoolRepository: SchoolRepository,
        baseDataRepository: BaseDataRepository,
        profileRepository: ProfileRepository,
        vPlanRepository: VPlanRepository,
        vppIdRepository: VppIdRepository,
        keyValueRepository: KeyValueRepository
    ) = CheckCredentialsAndInitOnboardingForSchoolUseCase(
        schoolRepository = schoolRepository,
        baseDataRepository = baseDataRepository,
        profileRepository = profileRepository,
        vPlanRepository = vPlanRepository,
        vppIdRepository = vppIdRepository,
        keyValueRepository = keyValueRepository
    )
}