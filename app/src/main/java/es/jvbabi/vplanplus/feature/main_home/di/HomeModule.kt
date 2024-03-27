package es.jvbabi.vplanplus.feature.main_home.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TimeRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetColorSchemeUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetVersionHintsUseCase
import es.jvbabi.vplanplus.domain.usecase.home.MainUseCases
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.IsInfoExpandedUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.SetInfoExpandedUseCase
import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.home.GetAppThemeUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.ChangeProfileUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetDayForCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetLastSyncUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.GetRoomBookingsForTodayUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.UpdateLastVersionHintsVersionUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetProfilesUseCase as GetProfilesUseCase1

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideMainUseCases(
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
            getAppThemeUseCase = GetAppThemeUseCase(keyValueRepository)
        )
    }

    @Provides
    @Singleton
    fun provideHomeUseCases(
        @ApplicationContext context: Context,
        profileRepository: ProfileRepository,
        vppIdRepository: VppIdRepository,
        keyValueRepository: KeyValueRepository,
        classRepository: ClassRepository,
        roomRepository: RoomRepository,
        planRepository: PlanRepository,
        timeRepository: TimeRepository,
        homeworkRepository: HomeworkRepository
    ): es.jvbabi.vplanplus.feature.main_home.domain.usecase.HomeUseCases {
        val getCurrentIdentityUseCase = GetCurrentIdentityUseCase(
            profileRepository = profileRepository,
            vppIdRepository = vppIdRepository,
            keyValueRepository = keyValueRepository,
            classRepository = classRepository,
        )
        return es.jvbabi.vplanplus.feature.main_home.domain.usecase.HomeUseCases(
            getProfilesUseCase = GetProfilesUseCase1(
                profileRepository = profileRepository
            ),
            getCurrentIdentityUseCase = getCurrentIdentityUseCase,
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
            getHomeworkUseCase = es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.GetHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                keyValueRepository = keyValueRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase
            ),
            isInfoExpandedUseCase = IsInfoExpandedUseCase(
                keyValueRepository = keyValueRepository
            ),
            setInfoExpandedUseCase = SetInfoExpandedUseCase(
                keyValueRepository = keyValueRepository
            ),
            changeProfileUseCase = ChangeProfileUseCase(keyValueRepository),
            isSyncRunningUseCase = IsSyncRunningUseCase(context),
            getVersionHintsUseCase = GetVersionHintsUseCase(keyValueRepository, vppIdRepository),
            updateLastVersionHintsVersionUseCase = UpdateLastVersionHintsVersionUseCase(keyValueRepository),
            getRoomBookingsForTodayUseCase = GetRoomBookingsForTodayUseCase(roomRepository)
        )
    }
}