package es.jvbabi.vplanplus.feature.settings.notifications.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsNotificationsEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.GetDailyReminderTimeForCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.IsDailyReminderEnabledForCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.NotificationSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.SetDailyReminderEnabledForCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.SetDailyReminderTimeForCurrentProfileUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationSettingsModule {

    @Provides
    @Singleton
    fun provideNotificationSettingsUseCases(
        systemRepository: SystemRepository,
        dailyReminderRepository: DailyReminderRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase
    ) = NotificationSettingsUseCases(
        isNotificationsEnabledUseCase = IsNotificationsEnabledUseCase(systemRepository),

        isDailyReminderEnabledUseCase = IsDailyReminderEnabledForCurrentProfileUseCase(dailyReminderRepository, getCurrentProfileUseCase),
        setDailyReminderEnabledUseCase = SetDailyReminderEnabledForCurrentProfileUseCase(dailyReminderRepository, getCurrentProfileUseCase),
        getDailyReminderTimeUseCase = GetDailyReminderTimeForCurrentProfileUseCase(dailyReminderRepository, getCurrentProfileUseCase),
        setDailyReminderTimeUseCase = SetDailyReminderTimeForCurrentProfileUseCase(dailyReminderRepository, getCurrentProfileUseCase)
    )
}