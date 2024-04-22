package es.jvbabi.vplanplus.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetAppThemeUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetColorSchemeUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetHomeworkUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetSyncIntervalMinutesUseCase
import es.jvbabi.vplanplus.domain.usecase.home.MainUseCases
import es.jvbabi.vplanplus.domain.usecase.home.SetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
    @Provides
    @Singleton
    fun provideMainUseCases(
        keyValueRepository: KeyValueRepository,
        homeworkRepository: HomeworkRepository,
        setUpUseCase: SetUpUseCase,
        profileRepository: ProfileRepository,
        getProfilesUseCase: GetProfilesUseCase
    ): MainUseCases {
        return MainUseCases(
            getColorSchemeUseCase = GetColorSchemeUseCase(keyValueRepository),
            getCurrentIdentity = GetCurrentIdentityUseCase(
                keyValueRepository = keyValueRepository,
                profileRepository = profileRepository,
            ),
            getProfilesUseCase = getProfilesUseCase,
            setUpUseCase = setUpUseCase,
            getHomeworkUseCase = GetHomeworkUseCase(homeworkRepository),
            getAppThemeUseCase = GetAppThemeUseCase(keyValueRepository),
            getSyncIntervalMinutesUseCase = GetSyncIntervalMinutesUseCase(keyValueRepository),
            setCurrentProfileUseCase = SetCurrentProfileUseCase(keyValueRepository, profileRepository)
        )
    }

    @Provides
    @Singleton
    fun provideSetUpUseCase(
        keyValueRepository: KeyValueRepository,
        homeworkRepository: HomeworkRepository,
        vppIdRepository: VppIdRepository,
        profileRepository: ProfileRepository,
        alarmManagerRepository: AlarmManagerRepository,
        firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
    ): SetUpUseCase {
        return SetUpUseCase(
            keyValueRepository = keyValueRepository,
            homeworkRepository = homeworkRepository,
            alarmManagerRepository = alarmManagerRepository,
            firebaseCloudMessagingManagerRepository = firebaseCloudMessagingManagerRepository,
            vppIdRepository = vppIdRepository,
            testForMissingVppIdToProfileConnectionsUseCase = TestForMissingVppIdToProfileConnectionsUseCase(vppIdRepository, profileRepository)
        )
    }
}