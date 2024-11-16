package es.jvbabi.vplanplus.feature.settings.profile.notifications.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.usecase.daily.SendNotificationUseCase
import es.jvbabi.vplanplus.domain.usecase.daily.UpdateDailyNotificationAlarmsUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsDeveloperModeEnabledUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.shared.GetProfileByIdUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.CanSendProfileNotificationsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.GetDailyReminderTimeUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.ProfileNotificationSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.SetDailyReminderEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.SetDailyReminderTimeUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.SetNotificationOnNewAssessmentUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.SetNotificationOnNewPlanUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.ToggleNotificationForProfileUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.ToggleSendNotificationOnNewHomeworkUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileNotificationModule {

    @Provides
    @Singleton
    fun provideProfileNotificationSettingsUseCases(
        systemRepository: SystemRepository,
        profileRepository: ProfileRepository,
        keyValueRepository: KeyValueRepository,
        dailyReminderRepository: DailyReminderRepository,
        sendNotificationUseCase: SendNotificationUseCase,
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        getProfileByIdUseCase: GetProfileByIdUseCase,
        updateDailyNotificationAlarmsUseCase: UpdateDailyNotificationAlarmsUseCase
    ) = ProfileNotificationSettingsUseCases(
        getProfileByIdUseCase = getProfileByIdUseCase,
        isDeveloperModeEnabledUseCase = IsDeveloperModeEnabledUseCase(keyValueRepository),
        canSendProfileNotificationsUseCase = CanSendProfileNotificationsUseCase(systemRepository),

        toggleSendNotificationOnNewHomeworkUseCase = ToggleSendNotificationOnNewHomeworkUseCase(profileRepository, getCurrentProfileUseCase),
        setNotificationOnNewAssessmentUseCase = SetNotificationOnNewAssessmentUseCase(profileRepository),

        toggleNotificationForProfileUseCase = ToggleNotificationForProfileUseCase(profileRepository),
        setNotificationOnNewPlanUseCase = SetNotificationOnNewPlanUseCase(profileRepository),

        setDailyReminderEnabledUseCase = SetDailyReminderEnabledUseCase(profileRepository, updateDailyNotificationAlarmsUseCase),
        getDailyReminderTimeUseCase = GetDailyReminderTimeUseCase(dailyReminderRepository),
        setDailyReminderTimeUseCase = SetDailyReminderTimeUseCase(dailyReminderRepository, updateDailyNotificationAlarmsUseCase),
        sendNotificationUseCase = sendNotificationUseCase
    )
}