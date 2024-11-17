package es.jvbabi.vplanplus.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.daily.UpdateDailyNotificationAlarmsUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetAppThemeUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetColorSchemeUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetHomeworkUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetSyncIntervalMinutesUseCase
import es.jvbabi.vplanplus.domain.usecase.home.MainUseCases
import es.jvbabi.vplanplus.domain.usecase.home.SetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.UpdateFirebaseTokenUseCase
import es.jvbabi.vplanplus.domain.usecase.update.EnableAssessmentsOnlyForCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.web_auth.GetWebAuthTaskUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.web_auth.PickEmojiUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.web_auth.WebAuthTaskUseCases
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
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
        getProfilesUseCase: GetProfilesUseCase,
        getCurrentProfileUseCase: GetCurrentProfileUseCase
    ): MainUseCases {
        return MainUseCases(
            getColorSchemeUseCase = GetColorSchemeUseCase(keyValueRepository),
            getCurrentIdentity = GetCurrentProfileUseCase(
                keyValueRepository = keyValueRepository,
                profileRepository = profileRepository,
            ),
            getProfilesUseCase = getProfilesUseCase,
            setUpUseCase = setUpUseCase,
            getHomeworkUseCase = GetHomeworkUseCase(homeworkRepository, getCurrentProfileUseCase),
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
        updateDailyNotificationAlarmsUseCase: UpdateDailyNotificationAlarmsUseCase,
        updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase
    ): SetUpUseCase {
        return SetUpUseCase(
            keyValueRepository = keyValueRepository,
            homeworkRepository = homeworkRepository,
            vppIdRepository = vppIdRepository,
            testForMissingVppIdToProfileConnectionsUseCase = TestForMissingVppIdToProfileConnectionsUseCase(vppIdRepository, profileRepository),
            updateFirebaseTokenUseCase = updateFirebaseTokenUseCase,
            updateDailyNotificationAlarmsUseCase = updateDailyNotificationAlarmsUseCase,
            enableAssessmentsOnlyForCurrentProfileUseCase = EnableAssessmentsOnlyForCurrentProfileUseCase(profileRepository, keyValueRepository),
            setBalloonUseCase = SetBalloonUseCase(keyValueRepository)
        )
    }

    @Provides
    @Singleton
    fun provideWebAuthTaskUseCases(
        vppIdRepository: VppIdRepository,
        logRepository: LogRecordRepository,
        systemRepository: SystemRepository,
        notificationRepository: NotificationRepository,
        stringRepository: StringRepository
    ): WebAuthTaskUseCases {
        return WebAuthTaskUseCases(
            getWebAuthTaskUseCase = GetWebAuthTaskUseCase(
                vppIdRepository = vppIdRepository,
                logRepository = logRepository,
                systemRepository = systemRepository,
                notificationRepository = notificationRepository,
                stringRepository = stringRepository
            ),
            pickEmojiUseCase = PickEmojiUseCase(vppIdRepository)
        )
    }
}