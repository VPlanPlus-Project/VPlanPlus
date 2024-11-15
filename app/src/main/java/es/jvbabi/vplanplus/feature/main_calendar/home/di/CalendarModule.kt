package es.jvbabi.vplanplus.feature.main_calendar.home.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.CalendarViewUseCases
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.CanShowTimetableInfoBannerUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.DismissTimetableInfoBannerUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.GetDayUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetCurrentDataVersionUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetLastSyncUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    @Provides
    @Singleton
    fun provideCalendarViewUseCases(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        keyValueRepository: KeyValueRepository,
        getDayUseCase: es.jvbabi.vplanplus.domain.usecase.general.GetDayUseCase
    ): CalendarViewUseCases = CalendarViewUseCases(
        getCurrentProfileUseCase = getCurrentProfileUseCase,
        getDayUseCase = GetDayUseCase(
            getCurrentProfileUseCase = getCurrentProfileUseCase,
            getDayUseCase = getDayUseCase
        ),
        getLastSyncUseCase = GetLastSyncUseCase(keyValueRepository),

        canShowTimetableInfoBannerUseCase = CanShowTimetableInfoBannerUseCase(keyValueRepository),
        dismissTimetableInfoBannerUseCase = DismissTimetableInfoBannerUseCase(keyValueRepository),
        getCurrentDataVersionUseCase = GetCurrentDataVersionUseCase(keyValueRepository)
    )
}