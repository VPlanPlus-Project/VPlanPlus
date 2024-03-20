package es.jvbabi.vplanplus.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.repository.CalendarRepositoryImpl
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.usecase.calendar.UpdateCalendarUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    @Provides
    @Singleton
    fun provideCalendarRepository(@ApplicationContext context: Context): CalendarRepository {
        return CalendarRepositoryImpl(context = context)
    }

    @Provides
    @Singleton
    fun provideUpdateCalendarUseCase(
        calendarRepository: CalendarRepository,
        keyValueRepository: KeyValueRepository,
        planRepository: PlanRepository,
        profileRepository: ProfileRepository,
        stringRepository: StringRepository
    ): UpdateCalendarUseCase {
        return UpdateCalendarUseCase(
            profileRepository = profileRepository,
            calendarRepository = calendarRepository,
            keyValueRepository = keyValueRepository,
            planRepository = planRepository,
            stringRepository = stringRepository
        )
    }
}