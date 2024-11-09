package es.jvbabi.vplanplus.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.usecase.daily.SendNotificationUseCase
import es.jvbabi.vplanplus.domain.usecase.daily.UpdateDailyNotificationAlarmsUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetNextDayUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DailyNotificationModule {

    @Provides
    @Singleton
    fun provideSendNotificationUseCase(
        profileRepository: ProfileRepository,
        dailyReminderRepository: DailyReminderRepository,
        notificationRepository: NotificationRepository,
        stringRepository: StringRepository,
        getNextDayUseCase: GetNextDayUseCase
    ): SendNotificationUseCase {
        return SendNotificationUseCase(
            profileRepository = profileRepository,
            dailyReminderRepository = dailyReminderRepository,
            notificationRepository = notificationRepository,
            stringRepository = stringRepository,
            getNextDayUseCase = getNextDayUseCase
        )
    }

    @Provides
    @Singleton
    fun provideUpdateDailyNotificationAlarmsUseCase(
        alarmManagerRepository: AlarmManagerRepository,
        profileRepository: ProfileRepository,
        dailyReminderRepository: DailyReminderRepository
    ): UpdateDailyNotificationAlarmsUseCase {
        return UpdateDailyNotificationAlarmsUseCase(
            alarmManagerRepository = alarmManagerRepository,
            profileRepository = profileRepository,
            dailyReminderRepository = dailyReminderRepository
        )
    }
}