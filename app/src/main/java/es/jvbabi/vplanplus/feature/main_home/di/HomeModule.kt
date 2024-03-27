package es.jvbabi.vplanplus.feature.main_home.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.ChangeProfileUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetCurrentDataVersionUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetDayUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetLastSyncUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetProfilesUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetRoomBookingsForTodayUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetVersionHintsUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.HasUnreadNewsUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetHideFinishedLessonsUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.HomeUseCases
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.IsInfoExpandedUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.SetInfoExpandedUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.UpdateLastVersionHintsVersionUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideHomUseCases(
        keyValueRepository: KeyValueRepository,
        planRepository: PlanRepository,
        homeworkRepository: HomeworkRepository,
        roomRepository: RoomRepository,
        profileRepository: ProfileRepository,
        messageRepository: MessageRepository,
        vppIdRepository: VppIdRepository,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
        getCurrentTimeUseCase: GetCurrentTimeUseCase,
        @ApplicationContext context: Context
    ): HomeUseCases {
        return HomeUseCases(
            getCurrentIdentityUseCase = getCurrentIdentityUseCase,
            getCurrentTimeUseCase = getCurrentTimeUseCase,
            getDayUseCase = GetDayUseCase(
                planRepository = planRepository,
                getCurrentDataVersionUseCase = GetCurrentDataVersionUseCase(keyValueRepository)
            ),
            getProfilesUseCase = GetProfilesUseCase(profileRepository),
            changeProfileUseCase = ChangeProfileUseCase(keyValueRepository),
            getHomeworkUseCase = GetHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                keyValueRepository = keyValueRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase
            ),
            getRoomBookingsForTodayUseCase = GetRoomBookingsForTodayUseCase(roomRepository),
            isSyncRunningUseCase = IsSyncRunningUseCase(context),
            getLastSyncUseCase = GetLastSyncUseCase(keyValueRepository),
            getHideFinishedLessonsUseCase = GetHideFinishedLessonsUseCase(keyValueRepository),

            setInfoExpandedUseCase = SetInfoExpandedUseCase(keyValueRepository),
            isInfoExpandedUseCase = IsInfoExpandedUseCase(keyValueRepository),

            hasUnreadNewsUseCase = HasUnreadNewsUseCase(messageRepository),

            getVersionHintsUseCase = GetVersionHintsUseCase(keyValueRepository, vppIdRepository),
            updateLastVersionHintsVersionUseCase = UpdateLastVersionHintsVersionUseCase(keyValueRepository)
        )
    }
}