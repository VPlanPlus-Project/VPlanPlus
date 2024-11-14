package es.jvbabi.vplanplus.feature.main_home.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetNextDayUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.GetDayUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.ChangeProfileUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetHideFinishedLessonsUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetHolidaysUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetLastSyncUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetNextSchoolDayUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetProfilesUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetRoomBookingsForTodayUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetVersionHintsUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.HasInvalidVppIdSessionUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.HasMissingVppIdToProfileLinksUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.HasUnreadNewsUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.HomeUseCases
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.IgnoreInvalidVppIdSessionsUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.IsInfoExpandedUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.UpdateLastVersionHintsVersionUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideGetDayUseCase(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        getDayUseCase: es.jvbabi.vplanplus.domain.usecase.general.GetDayUseCase,
    ): GetDayUseCase {
        return GetDayUseCase(
            getCurrentProfileUseCase = getCurrentProfileUseCase,
            getDayUseCase = getDayUseCase,
        )
    }

    @Provides
    @Singleton
    fun provideHomeUseCases(
        keyValueRepository: KeyValueRepository,
        homeworkRepository: HomeworkRepository,
        roomRepository: RoomRepository,
        profileRepository: ProfileRepository,
        messageRepository: MessageRepository,
        vppIdRepository: VppIdRepository,
        holidayRepository: HolidayRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        getCurrentTimeUseCase: GetCurrentTimeUseCase,
        getDayUseCase: GetDayUseCase,
        getNextDayUseCase: GetNextDayUseCase,
        @ApplicationContext context: Context
    ): HomeUseCases {
        return HomeUseCases(
            getCurrentProfileUseCase = getCurrentProfileUseCase,
            getCurrentTimeUseCase = getCurrentTimeUseCase,
            getDayUseCase = getDayUseCase,
            getProfilesUseCase = GetProfilesUseCase(profileRepository),
            changeProfileUseCase = ChangeProfileUseCase(keyValueRepository),
            getHomeworkUseCase = GetHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase,
            ),
            getRoomBookingsForTodayUseCase = GetRoomBookingsForTodayUseCase(roomRepository),
            isSyncRunningUseCase = IsSyncRunningUseCase(context),
            getLastSyncUseCase = GetLastSyncUseCase(keyValueRepository),
            getHideFinishedLessonsUseCase = GetHideFinishedLessonsUseCase(keyValueRepository),
            getHolidaysUseCase = GetHolidaysUseCase(holidayRepository, getCurrentProfileUseCase),

            isInfoExpandedUseCase = IsInfoExpandedUseCase(keyValueRepository),

            hasUnreadNewsUseCase = HasUnreadNewsUseCase(messageRepository),

            getVersionHintsUseCase = GetVersionHintsUseCase(keyValueRepository, vppIdRepository),
            updateLastVersionHintsVersionUseCase = UpdateLastVersionHintsVersionUseCase(keyValueRepository),

            hasInvalidVppIdSessionUseCase = HasInvalidVppIdSessionUseCase(keyValueRepository),
            ignoreInvalidVppIdSessionsUseCase = IgnoreInvalidVppIdSessionsUseCase(keyValueRepository),
            hasMissingVppIdToProfileLinksUseCase = HasMissingVppIdToProfileLinksUseCase(keyValueRepository),

            getVppIdServerUseCase = GetVppIdServerUseCase(keyValueRepository),
            getNextSchoolDayUseCase = GetNextSchoolDayUseCase(
                getCurrentProfileUseCase = getCurrentProfileUseCase,
                getNextDayUseCase = getNextDayUseCase
            )
        )
    }
}