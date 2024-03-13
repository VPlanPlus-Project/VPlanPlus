package es.jvbabi.vplanplus.feature.home.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetColorSchemeUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetHomeworkUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetVersionHintsUseCase
import es.jvbabi.vplanplus.domain.usecase.home.HomeUseCases
import es.jvbabi.vplanplus.domain.usecase.home.IsInfoExpandedUseCase
import es.jvbabi.vplanplus.domain.usecase.home.SetInfoExpandedUseCase
import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase
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
    ): HomeUseCases {
        return HomeUseCases(
            getColorSchemeUseCase = GetColorSchemeUseCase(keyValueRepository),
            getCurrentIdentity = GetCurrentIdentityUseCase(
                vppIdRepository = vppIdRepository,
                classRepository = classRepository,
                keyValueRepository = keyValueRepository,
                profileRepository = profileRepository,
            ),
            getProfilesUseCase = getProfilesUseCase,
            setUpUseCase = setUpUseCase,
            isInfoExpandedUseCase = IsInfoExpandedUseCase(keyValueRepository),
            setInfoExpandedUseCase = SetInfoExpandedUseCase(keyValueRepository),
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
            )
        )
    }
}