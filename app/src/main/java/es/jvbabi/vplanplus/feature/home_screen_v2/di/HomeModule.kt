package es.jvbabi.vplanplus.feature.home_screen_v2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.GetCurrentDataVersionUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.GetDayUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.GetRoomBookingsForTodayUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.HomeUseCases
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.IsInfoExpandedUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.SetInfoExpandedUseCase
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
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
        getCurrentTimeUseCase: GetCurrentTimeUseCase
    ): HomeUseCases {
        return HomeUseCases(
            getCurrentIdentityUseCase = getCurrentIdentityUseCase,
            getCurrentTimeUseCase = getCurrentTimeUseCase,
            getDayUseCase = GetDayUseCase(
                planRepository = planRepository,
                getCurrentDataVersionUseCase = GetCurrentDataVersionUseCase(keyValueRepository)
            ),
            getHomeworkUseCase = GetHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                keyValueRepository = keyValueRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase
            ),
            getRoomBookingsForTodayUseCase = GetRoomBookingsForTodayUseCase(roomRepository),

            setInfoExpandedUseCase = SetInfoExpandedUseCase(keyValueRepository),
            isInfoExpandedUseCase = IsInfoExpandedUseCase(keyValueRepository)
        )
    }
}