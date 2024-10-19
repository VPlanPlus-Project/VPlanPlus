package es.jvbabi.vplanplus.feature.settings.notifications.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.domain.usecase.general.IsNotificationsEnabledUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.repository.NdpUsageRepository
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.TriggerNdpReminderNotificationUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.UpdateDynamicTimesUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.IsAutomaticReminderTimeEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.IsNotificationOnNewHomeworkEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.NotificationSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.SetAutomaticReminderTimeEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.ToggleShowNotificationOnNewHomeworkUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationSettingsModule {

    @Singleton
    @Provides
    fun provideNotificationSettingsUseCases(
        systemRepository: SystemRepository,
        keyValueRepository: KeyValueRepository,
        lessonRepository: LessonRepository,
        timetableRepository: TimetableRepository,
        profileRepository: ProfileRepository,
        alarmManagerRepository: AlarmManagerRepository,
        ndpUsageRepository: NdpUsageRepository,
        triggerNdpReminderNotificationUseCase: TriggerNdpReminderNotificationUseCase
    ): NotificationSettingsUseCases {
        return NotificationSettingsUseCases(
            isNotificationsEnabledUseCase = IsNotificationsEnabledUseCase(systemRepository),

            isAutomaticReminderTimeEnabledUseCase = IsAutomaticReminderTimeEnabledUseCase(keyValueRepository),
            setAutomaticReminderTimeEnabledUseCase = SetAutomaticReminderTimeEnabledUseCase(keyValueRepository),

            developerUpdateDynamicTimesUseCase = UpdateDynamicTimesUseCase(
                lessonRepository = lessonRepository,
                timetableRepository = timetableRepository,
                profileRepository = profileRepository,
                keyValueRepository = keyValueRepository,
                alarmManagerRepository = alarmManagerRepository,
                ndpUsageRepository = ndpUsageRepository
            ),
            developerTriggerNdpReminderNotificationUseCase = triggerNdpReminderNotificationUseCase,

            isNotificationOnNewHomeworkEnabledUseCase = IsNotificationOnNewHomeworkEnabledUseCase(keyValueRepository),
            toggleNotificationOnNewHomeworkUseCase = ToggleShowNotificationOnNewHomeworkUseCase(keyValueRepository)
        )
    }
}