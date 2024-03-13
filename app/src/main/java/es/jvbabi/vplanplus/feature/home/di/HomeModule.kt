package es.jvbabi.vplanplus.feature.home.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.TimeRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetColorSchemeUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetHomeworkUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetVersionHintsUseCase
import es.jvbabi.vplanplus.domain.usecase.home.MainUseCases
import es.jvbabi.vplanplus.feature.home.domain.usecase.IsInfoExpandedUseCase
import es.jvbabi.vplanplus.feature.home.domain.usecase.SetInfoExpandedUseCase
import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.feature.home.domain.usecase.GetDayForCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.home.domain.usecase.GetLastSyncUseCase
import es.jvbabi.vplanplus.feature.home.domain.usecase.UpdateLastVersionHintsVersionUseCase
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton
import es.jvbabi.vplanplus.feature.home.domain.usecase.GetProfilesUseCase as GetProfilesUseCase1

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    @Deprecated("")
    fun provideLegacyHomeUseCases(
        keyValueRepository: KeyValueRepository,
        classRepository: ClassRepository,
        vppIdRepository: VppIdRepository,
        homeworkRepository: HomeworkRepository,
        setUpUseCase: SetUpUseCase,
        profileRepository: ProfileRepository,
        getProfilesUseCase: GetProfilesUseCase
    ): MainUseCases {
        return MainUseCases(
            getColorSchemeUseCase = GetColorSchemeUseCase(keyValueRepository),
            getCurrentIdentity = GetCurrentIdentityUseCase(
                vppIdRepository = vppIdRepository,
                classRepository = classRepository,
                keyValueRepository = keyValueRepository,
                profileRepository = profileRepository,
            ),
            getProfilesUseCase = getProfilesUseCase,
            setUpUseCase = setUpUseCase,
            getHomeworkUseCase = GetHomeworkUseCase(homeworkRepository),
            getVersionHintsUseCase = GetVersionHintsUseCase(keyValueRepository, vppIdRepository),
            updateLastVersionHintsVersionUseCase = UpdateLastVersionHintsVersionUseCase(keyValueRepository)
        )
    }

    @Provides
    @Singleton
    fun provideHomeUseCases(
        profileRepository: ProfileRepository,
        vppIdRepository: VppIdRepository,
        keyValueRepository: KeyValueRepository,
        classRepository: ClassRepository,
        planRepository: PlanRepository,
        timeRepository: TimeRepository,
        homeworkRepository: HomeworkRepository
    ): es.jvbabi.vplanplus.feature.home.domain.usecase.HomeUseCases {
        return es.jvbabi.vplanplus.feature.home.domain.usecase.HomeUseCases(
            getProfilesUseCase = GetProfilesUseCase1(
                profileRepository = profileRepository
            ),
            getCurrentIdentityUseCase = GetCurrentIdentityUseCase(
                profileRepository = profileRepository,
                vppIdRepository = vppIdRepository,
                keyValueRepository = keyValueRepository,
                classRepository = classRepository,
            ),
            getDayForCurrentProfileUseCase = GetDayForCurrentProfileUseCase(
                keyValueRepository = keyValueRepository,
                profileRepository = profileRepository,
                planRepository = planRepository
            ),
            getLastSyncUseCase = GetLastSyncUseCase(
                keyValueRepository = keyValueRepository
            ),
            getCurrentTimeUseCase = GetCurrentTimeUseCase(
                timeRepository = timeRepository
            ),
            getHomeworkUseCase = es.jvbabi.vplanplus.feature.home.domain.usecase.GetHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                keyValueRepository = keyValueRepository
            ),
            isInfoExpandedUseCase = IsInfoExpandedUseCase(
                keyValueRepository = keyValueRepository
            ),
            setInfoExpandedUseCase = SetInfoExpandedUseCase(
                keyValueRepository = keyValueRepository
            )
        )
    }
}