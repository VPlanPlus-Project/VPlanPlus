package es.jvbabi.vplanplus.feature.settings.notifications.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.usecase.general.IsNotificationsEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.IsNotificationOnNewHomeworkEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.NotificationSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.SetShowNotificationOnNewHomeworkUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationSettingsModule {

    @Provides
    @Singleton
    fun provideNotificationSettingsUseCases(
        systemRepository: SystemRepository,
        keyValueRepository: KeyValueRepository
    ) = NotificationSettingsUseCases(
        isNotificationsEnabledUseCase = IsNotificationsEnabledUseCase(systemRepository),
        isNotificationOnNewHomeworkEnabledUseCase = IsNotificationOnNewHomeworkEnabledUseCase(keyValueRepository),
        setShowNotificationOnNewHomeworkUseCase = SetShowNotificationOnNewHomeworkUseCase(keyValueRepository)
    )
}